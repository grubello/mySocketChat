const admin = require("firebase-admin"); 
const utils = require("../utils");

const messageService = (io) => {
    io.on('connection', (socket) => {
        console.log(`Client ${socket.id} is connected to message services`);

        isConnection(socket,io);
        sendMessage(socket,io);
        clearMessagesRead(socket,io);

    });
}

//send message to friend
function sendMessage(socket,io){
    socket.on('messageSent',(data) => {
 
        var friendMessageReference = admin.database().ref('userMessages')
                    .child(utils.encodeEmail(data.friendEmail))
                    .child(utils.encodeEmail(data.senderEmail))
                    .push();

        var friendNewMessageReference = admin.database().ref('userNewMessages')
                    .child(utils.encodeEmail(data.friendEmail))
                    .child(friendMessageReference.key);

        var chatroomReference = admin.database().ref('chatrooms')
                    .child(utils.encodeEmail(data.friendEmail))
                    .child(utils.encodeEmail(data.senderEmail));
        
        var chatroom = {
            friendPicture: data.senderPicture,
            friendName: data.senderName,
            friendEmail: data.senderEmail,
            lastMessage: data.messageText,
            lastMessageSenderEmail: data.senderEmail,
            lastMessageRead: false,
            sentLastMessage:  true
        };

        var message = {
            id: data.messageId,
            messageText: data.messageText,
            email: data.senderEmail,
            picture: data.senderPicture
        };

        //create/update chatroom, pass message and and newMessage
        chatroomReference.set(chatroom);
        friendMessageReference.set(message);
        friendNewMessageReference.set(message);

    });
}

//clear messages that are read
function clearMessagesRead(socket,io){
    socket.on('clearMessagesRead',(data) => {

        var messageIds = data.messageIds;
        var userEmail = utils.encodeEmail(data.userEmail);

        var userNewMessagesReference = admin.database().ref('userNewMessages')
                                        .child(userEmail);

            userNewMessagesReference.once('value', (snapshot) => {
                snapshot.forEach((messageKey) => {
                    //if messageId is in messageIds array
                    if (messageIds.indexOf(messageKey.val().id) > -1){
                        userNewMessagesReference.child(messageKey.key).remove();
                    }
                });
            });
       
    });
}

//detect disconnect
function isConnection(socket, io){
    socket.on('disconnect', () =>{
        console.log("disconnected from message services");
    });
}


module.exports = {messageService}