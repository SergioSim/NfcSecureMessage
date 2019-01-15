const jwt = require('jsonwebtoken');
var mysql = require('mysql');

var con = mysql.createConnection({
	host: "82.255.166.104",
	user: "GrailsUser",
	password: "GrailsPassword13?"
  });

exports.sendMessage = function(req,res){

        var data = {   
            userLOGIN : req.userLOGIN,
            message : req.message,
		   };		
		   	
	    	con.query("INSERT INTO GrailsUser.nfc_message  (message,userLOGIN) VALUES (?,?)",[data.message,data.userLOGIN],function(err, result){
				if (err) {
					console.log(result+"result")
					console.log(err)
					Response = {
						succes: false,
						msg :"message non envoyé" }
				}
				else{
					Response = {
						succes: true,
						msg :"message envoyé avec succèes",
						resultat: result }
				}
				res(Response);
		  } 				

	   );
	}

exports.createUser = function(req,res){

        var data = {   
            userLOGIN : req.login,
            userPASSWORD : req.password,
		   };		
		   	
	    	con.query("INSERT INTO GrailsUser.nfc_user  (login,password) VALUES (?,?)",[data.userLOGIN,data.userPASSWORD],function(err, result){
				if (err) {
					Response = {
						succes: false,
						msg :"utiisateur non ajouté" }
				}
				else{
					Response = {
						succes: true,
						msg :"utilisateur ajouté ",
						resultat: result }
				}
				res(Response);
		  } 				

	   );
	}


exports.login = function(req,res){

        var data = {   
            userLOGIN : req.login,
            userPASSWORD : req.password,
		   };		

	    	con.query("SELECT * FROM GrailsUser.nfc_user WHERE login=? AND password=? ",[data.userLOGIN,data.userPASSWORD],function(err, result){

				if (err) {
							Response = {
								succes: false,
								msg :"erreur de connexion" }
				        }
					else{
							if(result.length ==0){
								Response = {
									succes: false,
									msg :"utiisateur non TROUVE" }
							}
							else {
								let user={id:result.id}
									Response = {
										succes: true,
										msg :"connexion reussi ",
										resultat: result,
										access_token:jwt.sign({user}, 'secretkey')
									}
						}
					}
				res(Response);
		  } 				

	);
}



