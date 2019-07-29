var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var admin = require("firebase-admin");
const googleStorage = require('@google-cloud/storage');

var serviceAccount = require("./private/credentials.json");
var accountServices = require("./firebase/services.js");
var friendServices = require("./firebase/friendServices.js");
var messageServices = require("./firebase/messageServices.js");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "yourdatabaseurl"
});



//handle requests with io and socket
accountServices.userAccountRequests(io);
friendServices.userFriendRequests(io);
messageServices.messageService(io);


http.listen(3000, () => {
    console.log('server listening on port 3000');
});

