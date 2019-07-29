const admin = require("firebase-admin"); 
const utils = require("../utils");
const fcm = require("./fcmpush");

const userFriendRequests = (io) => {
    io.on('connection', (socket) => {
    console.log(`Client ${socket.id} is connected to friend services`);

    isConnection(socket,io);
    sendOrCancelFriendRequest(socket,io);
    acceptOrDenyRequest(socket,io);
    removeFriend(socket,io);
    
    });
}

function sendOrCancelFriendRequest(socket, io){
    socket.on('friendRequest', (data, callback) => {

        var db = admin.database();
        var reference = db.ref('friendRequestReceived');
        var friendReference = reference.child(utils.encodeEmail(data.friendEmail)).child(utils.encodeEmail(data.userEmail));

        if(data.requestCode == 1){              //send request

            var userReference = db.ref('users').child(utils.encodeEmail(data.userEmail));
            userReference.once('value', (snapshot) => {
                friendReference.set({
                    email: snapshot.val().email,
                    hasLoggedIn: snapshot.val().hasLoggedIn,
                    userName: snapshot.val().userName,
                    userPicture: snapshot.val().userPicture
                });
            });

        } else if (data.requestCode == 0){     //cancel request
            friendReference.remove();
        }

        callback(true);

    });
}

function acceptOrDenyRequest(socket,io){
    socket.on('acceptOrDenyRequest', (data, callback) => {

        var userRequestReference = admin.database().ref('friends')
                                    .child(utils.encodeEmail(data.userEmail))
                                    .child(utils.encodeEmail(data.friendEmail));

        var userFriendRequestReference = admin.database().ref('friends')
                                    .child(utils.encodeEmail(data.friendEmail))
                                    .child(utils.encodeEmail(data.userEmail));

        var requestReceivedReference = admin.database().ref('friendRequestReceived')
                                    .child(utils.encodeEmail(data.userEmail))
                                    .child(utils.encodeEmail(data.friendEmail));

        var requestReceivedSecondUserReference = admin.database().ref('friendRequestReceived')
                                    .child(utils.encodeEmail(data.friendEmail))
                                    .child(utils.encodeEmail(data.userEmail));

        var requestSentReference = admin.database().ref('friendRequestSent')
                                    .child(utils.encodeEmail(data.userEmail))
                                    .child(utils.encodeEmail(data.friendEmail));

        var requestSentSecondUserReference = admin.database().ref('friendRequestSent')
                                    .child(utils.encodeEmail(data.friendEmail))
                                    .child(utils.encodeEmail(data.userEmail));

        //deny request
        if(data.requestCode == 0){                  
            //remove sent friend request for both users
            //remove received friend request for both users
            requestSentReference.remove();
            requestReceivedReference.remove();
            requestReceivedSecondUserReference.remove();
            requestSentSecondUserReference.remove();
            callback(true);
        } 
        //accept request
        else if (data.requestCode == 1){          

            var userReference = admin.database().ref('users').child(utils.encodeEmail(data.userEmail));
            var friendReference = admin.database().ref('users').child(utils.encodeEmail(data.friendEmail));
            
            friendReference.once('value', (snapshot) => {
                userRequestReference.set({
                    email: snapshot.val().email,
                    hasLoggedIn: snapshot.val().hasLoggedIn,
                    userName: snapshot.val().userName,
                    userPicture: snapshot.val().userPicture
                });
            });

            userReference.once('value', (snapshot) => {
                userFriendRequestReference.set({
                    email: snapshot.val().email,
                    hasLoggedIn: snapshot.val().hasLoggedIn,
                    userName: snapshot.val().userName,
                    userPicture: snapshot.val().userPicture
                });
            });

            //remove sent friend request for both users
            //remove received friend request for both users
            requestSentReference.remove();
            requestReceivedReference.remove();
            requestReceivedSecondUserReference.remove();
            requestSentSecondUserReference.remove();

            var to = utils.encodeEmail(data.friendEmail);
            var title = 'Socket Chat notification'; 
            var body = `Friend request from ${data.userEmail}`;

            //send notification
            fcm.fcmPush(to,title, body);
            callback(true);  
        }
    });

}

//remove friend
function removeFriend(socket, io){
    socket.on('cancelFriend', (data, callback) => {

        var reference = admin.database().ref('friends');
        var userReference = reference.child(utils.encodeEmail(data.userEmail)).child(utils.encodeEmail(data.friendEmail));
        var friendReference = reference.child(utils.encodeEmail(data.friendEmail)).child(utils.encodeEmail(data.userEmail));

        userReference.remove();
        friendReference.remove();

        callback(true);
    });
}

function isConnection(socket, io){
    socket.on('disconnect', () =>{
        console.log("disconnected from friend services");
    });
}

module.exports = {userFriendRequests}