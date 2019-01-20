const express  	= require('express');
const http   	= require('http');
const multer 	= require('multer'); // pour les formulaires multiparts
const bodyParser= require('body-parser');
const mysqlDB 	= require('./crud-mysql');

var multerData 	= multer();
const app      	= express();
const server 	= http.Server(app);
const port     	= process.env.PORT || 8083;

app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());
server.listen(port); // Lance le serveur avec express

console.log("Serveur lancé sur le port : " + port);

app.use(function (req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
    res.header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE");
    next();
});

app.get('/api/fetchMessages', function(req, res) { 
	mysqlDB.fetchMessages(req.query, function(data) {
		res.send(JSON.stringify(data)); 
	});
});

app.post('/api/sendMsg', multerData.fields([]), function(req, res) {
	mysqlDB.sendMessage(req.body, function(data) {
		res.send(JSON.stringify(data)); 
	});
});

app.post('/api/createUser', multerData.fields([]), function(req, res) {
	mysqlDB.createUser(req.body, function(data) {
		res.send(JSON.stringify(data)); 
	});
});

app.post('/api/login', multerData.fields([]), function(req, res) {
	mysqlDB.login(req.body, function(data) {
		res.send(JSON.stringify(data)); 
	});
});
