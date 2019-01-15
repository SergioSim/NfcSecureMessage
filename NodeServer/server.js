const express  = require('express');
const app      = express();
const port     = process.env.PORT || 8080;
const server   = require('http').Server(app);

// pour les formulaires multiparts
var multer = require('multer');
var multerData = multer();

const mysqlDB = require('./crud-mysql');
console.log("on off")
// Pour les formulaires standards
const bodyParser = require('body-parser');
// pour les formulaires multiparts
var multer = require('multer');
var multerData = multer();

app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

// Lance le serveur avec express
server.listen(port);

console.log("Serveur lancé sur le port : " + port);


var mysql = require('mysql');


// connexion à la base distante

var con = mysql.createConnection({
  host: "82.255.166.104",
  user: "GrailsUser",
  password: "GrailsPassword13?"
});


app.use(function (req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
    res.header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE");
    next();
});

// Test de la connexion à la base
app.get('/api/connection', function(req, res) {

	con.connect(function(err) {
		if (err) 
		{ 
			res.send(JSON.stringify("not conneccted"));
		}
		 else
		{ 
			res.send(JSON.stringify("YES, you have Connected! to mysql"));
		}
	});
});



app.get('/api/fetchMessages', function(req, res) { 


	con.query("SELECT * FROM GrailsUser.nfc_message ", function (err, result) {
		if (err)
		{
			res.send(JSON.stringify("Erreur"));
		}
		else
		{ 
			res.send(JSON.stringify(result));
		}
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


