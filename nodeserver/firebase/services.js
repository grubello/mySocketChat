const admin = require("firebase-admin"); 
const utils = require("../utils")

var userAccountRequests = (io) => {
    io.on('connection', (socket) => {
    console.log(`Client ${socket.id} is connected to services`);

    isConnection(socket,io);
    registerUser(socket,io);
    loginUser(socket,io);
    updatePhoto(socket,io);

    });
}

//register
function registerUser(socket,io){
    socket.on('registerUserData', (data) => {

        var bucket = admin.storage().bucket("yourbucketurl");
        var file = bucket.file("/defaultPictures/login.png");

        //get default profile picture url
        file.getSignedUrl({
            action: 'read',
            expires: '12-31-2199'
        }).then(signedUrls => {
            var url = signedUrls[0];

            //create user in firebase auth
            admin.auth().createUser({
                email: data.email,
                displayName: data.username,
                password: data.password})
            .then((record) => {
                
                //create user in firebase database
                var db = admin.database();
                var reference = db.ref('users');
                var userReference = reference.child(utils.encodeEmail(data.email));
    
                var date = {
                    value: admin.database.ServerValue.TIMESTAMP
                }
    
                userReference.set({
                    email: data.email,
                    userName: data.username,
                    userPicture: url,
                    dateJoined: date,
                    hasLoggedIn: false
                });
                
                //emit success message to client
                Object.keys(io.sockets.sockets).forEach((id) => {
                    if (id == socket.id){
                        var message = {
                            messageText: "success"
                        }
                        io.to(id).emit('message', message);
                    }
                });
    
                console.log(`User ${data.username} registered successfully`);
            })
            .catch((error) => {
                Object.keys(io.sockets.sockets).forEach((id) => {
                    if (id == socket.id){
                        var message = {
                            messageText: error.message
                        }
                        io.to(id).emit('message', message);
                    }
                });
                console.log("error " + error.message);
            });
        }).catch((error) => console.log(error));
        
    });
}

//login
function loginUser(socket,io){
    socket.on('userLoginData', (data, fn)=>{

        admin.auth().getUserByEmail(data.email)
            .then((record) =>{
                var db = admin.database();
                var reference = db.ref('users');
                var userReference = reference.child(utils.encodeEmail(data.email));

                //create custom authtoken
                userReference.once('value', (snapshot) => {
                    
                    var additionalClaims = {
                        email: data.email
                    };

                    admin.auth().createCustomToken(record.uid, additionalClaims)
                        .then((customToken) => {
                            Object.keys(io.sockets.sockets).forEach((id) => {
                                if (id == socket.id){
                                    var message = {
                                        messageText: "success",
                                        token: customToken,
                                        email: data.email,
                                        photo: snapshot.val().userPicture,
                                        displayName: snapshot.val().userName
                                    }

                                    userReference.child('hasLoggedIn').set(true);
                                    //fn(true);
                                    io.to(id).emit('token', message);
                                }
                            });
                        })
                        .catch((error) => {
                            Object.keys(io.sockets.sockets).forEach((id) => {
                                if (id == socket.id){
                                    var message = {
                                        messageText: "error",
                                        message: error
                                    }
                                    io.to(id).emit('token', message);
                                    console.log(message);
                                }
                            });
                            console.log(error);
                        });
                });
                
            })
            .catch((error) =>{
                console.log(error);
            });
    });
}

//update profile photo
function updatePhoto(socket,io){
    socket.on('updatePhoto', (data, callback)=>{

        var userEmail = data.userEmail;
        var userPhoto = data.userPhoto;

        var database = admin.database();
        var userMessagesReference = database.ref('userMessages');
        var chatroomsReference = database.ref('chatrooms');
        var friendsReference = database.ref('friends');
        var friendRequestSentReference = database.ref('friendRequestSent');
        var friendRequestReceivedReference = database.ref('friendRequestReceived');
        

        //update photo in all messages from user in userMessages node
        userMessagesReference.once('value', (snapshot) =>{
            snapshot.forEach((user) => {
                user.forEach((friendDetails) => {
                    friendDetails.forEach((messageDetails)=>{
                        if(messageDetails.val().email == userEmail){
                            userMessagesReference.child(user.key).child(friendDetails.key).child(messageDetails.key).set({
                                email: messageDetails.val().email,
                                id: messageDetails.val().id,
                                messageText: messageDetails.val().messageText,
                                picture: userPhoto
                            });
                        }
                    });
                });
            });
        });

        //update photo in all messages from user in chatrooms node
        chatroomsReference.once('value', (snapshot) =>{
            snapshot.forEach((user) => {
                user.forEach((chatRoomDetails) => {
                    if(user.key != utils.encodeEmail(userEmail) && chatRoomDetails.key == utils.encodeEmail(userEmail)){
                        chatroomsReference.child(user.key).child(chatRoomDetails.key).update({
                            friendPicture: userPhoto
                        });
                    }
                });
            });
        });

        //update photo in friends node
        friendsReference.once('value', (snapshot) => {
            snapshot.forEach((user) => {
                user.forEach((friendshipDetails) => {
                    if(user.key != utils.encodeEmail(userEmail) && friendshipDetails.key == utils.encodeEmail(userEmail)){
                        friendsReference.child(user.key).child(friendshipDetails.key).update({
                            userPicture: userPhoto
                        });
                    }
                });
            });
        });
    
        //update photo in friend requests sent node
        friendRequestSentReference.once('value', (snapshot) => {
            snapshot.forEach((user) => {
                user.forEach((requestDetails) => {
                    if(requestDetails.key == utils.encodeEmail(userEmail)){
                        friendRequestSentReference.child(user.key).child(requestDetails.key).update({
                            userPicture: userPhoto
                        });
                    }
                });
            });
        });

        //update photo in friend request received node
        friendRequestReceivedReference.once('value', (snapshot) => {
            snapshot.forEach((user) => {
                user.forEach((requestDetails) => {
                    if(requestDetails.key == utils.encodeEmail(userEmail)){
                        friendRequestReceivedReference.child(user.key).child(requestDetails.key).update({
                            userPicture: userPhoto
                        });
                    }
                });
            });
        });

        callback(true);
    });
}

//detect disconnect
function isConnection(socket, io){
    socket.on('disconnect', () =>{
        console.log("disconnected from register/login services");
    });
}

module.exports = {userAccountRequests}