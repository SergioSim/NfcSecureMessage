const jwt = require('jsonwebtoken');
const mysql = require('mysql');

const con = mysql.createConnection({
	host: "82.255.166.104",
	user: "GrailsUser",
	password: "GrailsPassword13?"});

exports.createUser = function(data, calback) {	
	con.query("INSERT INTO GrailsUser.nfc_user  (login,password) VALUES (?,?)", [data.login, data.password], function(err, result){
		calback({ succes: !err});
	});
}

exports.login = function(data, calback) {
	con.query("SELECT * FROM GrailsUser.nfc_user WHERE login=? AND password=? ",[data.login, data.password], function(err, result){
		Response = {
			succes: !err && result.length != 0,
			msg : !err && result.length == 0 ? "utiisateur non TROUVE" : "connexion reussi",
			access_token: !err ? jwt.sign({id:result.id}, 'secretkey') : ''}
		calback(Response);
	});
}

exports.sendMessage = function(data, calback) {
	con.query("INSERT INTO GrailsUser.nfc_message  (message,userLOGIN) VALUES (?,?)",[data.message, data.userLOGIN], function(err, result){
		calback({ succes: !err});
	});
}


exports.deleteMessage = function(id, calback) {
	con.query("DELETE FROM GrailsUser.nfc_message WHERE ID = ?",[id], function(err, result) {
		calback({ succes: !err});
	});	
}

exports.fetchMessages = function(data, calback) {
	con.query("SELECT * FROM GrailsUser.nfc_message WHERE userLOGIN = ?",[data.userLOGIN], function(err, result) {
		if(err) result = "Error";
		calback(result);
	});
}


