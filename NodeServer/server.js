const express  = require('express');
const app      = express();
const port     = process.env.PORT || 8080;
const server   = require('http').Server(app);

const jwt = require('jsonwebtoken');

// pour les formulaires multiparts
var multer = require('multer');
var multerData = multer();

const mongoDBModule = require('./app_modules/crud-mongo');

// Pour les formulaires standards
const bodyParser = require('body-parser');
// pour les formulaires multiparts
var multer = require('multer');
var multerData = multer();

// Cette ligne indique le répertoire qui contient
// les fichiers statiques: html, css, js, images etc.
app.use(express.static(__dirname + '/public'));
// Paramètres standards du modyle bodyParser
// qui sert à récupérer des paramètres reçus
// par ex, par l'envoi d'un formulaire "standard"
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

// Lance le serveur avec express
server.listen(port);

console.log("Serveur lancé sur le port : " + port);

//----------------------------------------------

// Test de la connexion à la base
app.get('/api/connection', function(req, res) {
	
   mongoDBModule.connexionMongo(function(err, db) {
   	let reponse;

   	if(err) {
   		console.log("erreur connexion");
   		reponse = {
   			msg: "erreur de connexion err=" + err
   		}
   	} else {
   		reponse = {
   			msg: "connexion établie"
   		}
   	}
   	res.send(JSON.stringify(reponse));

   });
});


app.use(function (req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
    res.header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE");
    next();
});


app.get('/api/fetchMessages', function(req, res) { 

	mongoDBModule.findMessages(function(data,count) {

		var objdData = {
			data: data,
		}
		res.send(JSON.stringify(objdData)); 
	}); 

});

app.post('/api/sendMsg', multerData.fields([]), function(req, res) {

	mongoDBModule.sendMessage(req.body, function(data) {

		res.send(JSON.stringify(data)); 

	});

});


app.post('/api/createUser', multerData.fields([]), function(req, res) {

	mongoDBModule.createUser(req.body, function(data) {

		res.send(JSON.stringify(data)); 

	});

});



app.post('/api/login', multerData.fields([]), function(req, res) {

	mongoDBModule.login(req.body, function(data) {

		res.send(JSON.stringify(data)); 
	});

});



