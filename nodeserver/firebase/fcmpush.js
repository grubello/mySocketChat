const admin = require("firebase-admin"); 
const FCM = require('fcm-push');

const serverKey = 'yourkey';
const fcm = new FCM(serverKey);

const fcmPush = (toEmailEncoded, title, body) => {

    const firendTokenReference = admin.database().ref('tokens').child(toEmailEncoded);

    firendTokenReference.once('value', (snapshot) =>{
        snapshot.forEach(child => {

            var to = child.val().token;
            console.log(to);
            var message = {
                to: to,
                notification: {
                    title: title,
                    body: body
                }
            };
        
            fcm.send(message)
                .then(function(response){
                    console.log("Successfully sent with response: ", response);
                })
                .catch(function(err){
                    console.log("Something went wrong!");
                    console.error(err);
                });
        });
    });
}

module.exports = {fcmPush}