const functions = require("firebase-functions/v1");
const admin = require("firebase-admin");

admin.initializeApp();
const db = admin.firestore();

/**
 * Cleans up all of a user's Firestore data when their Auth account is deleted.
 * Auth triggers are 1st gen only, hence the /v1 import.
 */
exports.onUserDeleted = functions
  .region("asia-south1")
  .auth.user()
  .onDelete(async (user) => {
    const uid = user.uid;
    console.log(`Cleaning up data for deleted user ${uid}`);

    try {
      // 1. The user's own profile document
      await db.collection("users").doc(uid).delete();

      // 2. Satsang requests they sent or received
      const [sent, received] = await Promise.all([
        db.collection("satsang_requests").where("fromUid", "==", uid).get(),
        db.collection("satsang_requests").where("toUid", "==", uid).get(),
      ]);

      const requestDeletes = [...sent.docs, ...received.docs].map((doc) =>
        doc.ref.delete()
      );
      await Promise.all(requestDeletes);
      console.log(`Deleted ${requestDeletes.length} satsang requests`);

      // 3. Conversations they're part of, including every message inside.
      //    recursiveDelete handles the subcollection in batches — this is the
      //    part a client cannot do safely.
      const conversations = await db
        .collection("conversations")
        .where("participants", "array-contains", uid)
        .get();

      for (const doc of conversations.docs) {
        await db.recursiveDelete(doc.ref);
      }
      console.log(`Deleted ${conversations.size} conversations`);

      // 4. Reports they filed (reports *about* them are kept for moderation)
      const reports = await db
        .collection("reports")
        .where("reporterUid", "==", uid)
        .get();
      await Promise.all(reports.docs.map((doc) => doc.ref.delete()));

      console.log(`Cleanup complete for ${uid}`);
    } catch (error) {
      console.error(`Cleanup failed for ${uid}`, error);
      throw error; // let Cloud Functions retry
    }
  });