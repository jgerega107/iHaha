const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendNotificationDMs = functions.firestore.document("users/{userUid}/notifications/{notificationId}").onCreate((snap, context) => {
	const messageObject = snap.data();
	const author = messageObject.author;
	const messageContent = messageObject.message;
	const sendToUid = messageObject.sendToUid;
	return admin.firestore().collection("users").doc(sendToUid).get().then(queryResult => {
		const fcmToken = queryResult.data().fcmToken;
			const notificationContent = {
				notification: {
					title: author,
					body: messageContent
				}
			};

			return admin.messaging().sendToDevice(fcmToken, notificationContent).then(result => {
				console.log("Notification sent!");
				return null;
			});

	});
});
