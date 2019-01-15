var MongoClient = require('mysql').MongoClient;
var ObjectId = require('mysql').ObjectID;
var assert = require('assert');
//var url = 'mongodb://localhost:27017/test';
const jwt = require('jsonwebtoken');
// Connection URL

// Database Name
const dbName = 'test';

exports.connexionMongo = function(callback) {
	MongoClient.connect(url, function(err, client) {
		var db = client.db(dbName);
		
		assert.equal(null, err);
		callback(err, db);
	});
}

exports.findMessages = function(callback) {
    MongoClient.connect(url, function(err, client) {

			var db = client.db(dbName);

			console.log("db " + db)
        if(!err){
                    db.collection('message')
                        .find()
                        .toArray()
                        .then(arr=>{
                            db.collection('message')
								.find()
                                .count()
                                .then(rep=>callback(arr,rep))
					});
			}       
        else{
            callback(-1);
        }
    });
};
var mysql = require('mysql');

var con = mysql.createConnection({
	host: "82.255.166.104",
	user: "GrailsUser",
	password: "GrailsPassword13?"
  });


exports.sendMessage = function(req,res){

        var data = {   
            user_id : req.userID,
            message : req.message,
		   };		
		   	
	    	con.query("INSERT INTO GrailsUser.nfc_message  (userID,message) VALUES (?,?)",[data.user_id,data.message],function(err, result){
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





		
	
/*
  
			if(!err){
				reponse = {
					succes : true,
					result: insertedId.ops[0]._id,
					error : null,
					msg: "Envoie de message reussi " + insertedId.ops[0]._id
				};
			} else {
				reponse = {
					succes : false,
					error : err,
					msg: "Problème à l'envoie de message"
				};
			}
			res.reponse;
		//	callback(reponse);
		});
	}
*/
/*

exports.sendMessage = function(formData, callback) {
	MongoClient.connect(url, function(err, client) {
		var db = client.db(dbName);

	    if(!err) {
	
            let toInsert = {
                author : formData.receiver,
                message : formData.message, 
            };
            
			console.dir(JSON.stringify(toInsert));
		    db.collection("message")
		    .insert(toInsert, function(err, insertedId) {
		    	let reponse;

		    	console.log('++++'+insertedId)

		        if(!err){
		            reponse = {
		                succes : true,
		                result: insertedId.ops[0]._id,
		                error : null,
		                msg: "Envoie de message reussi " + insertedId.ops[0]._id
		            };
		        } else {
		            reponse = {
		                succes : false,
		                error : err,
		                msg: "Problème à l'envoie de message"
		            };
		        }
		        callback(reponse);
		    });
		} else{
			let reponse = reponse = {
                    	succes: false,
                        error : err,
                        msg:"Problème lors de l'envoie, erreur de connexion."
                    };
            callback(reponse);
		}
	});
}

*/
/*
exports.createUser = function(formData, callback) {
	MongoClient.connect(url, function(err, client) {
		var db = client.db(dbName);

	    if(!err) {
	
            let toInsert = {
                username : formData.username,
                password : formData.password, 
            };
            
			console.dir(JSON.stringify(toInsert));
		    db.collection("utilisateur")
		    .insert(toInsert, function(err, insertedId) {
		    	let reponse;

		    	console.log('++++'+insertedId)

		        if(!err){
                    //{"succes":true,"result":"5c3b025ca94f0a32e447ee76","error":null,"msg":"Ajout réussi 5c3b025ca94f0a32e447ee76"}
		            reponse = {
		                succes : true,
		                result: insertedId.ops[0]._id,
						error : null,
		                msg: "Ajout réussi " + insertedId.ops[0]._id
		            };
		        } else {
		            reponse = {
		                succes : false,
		                error : err,
		                msg: "Problème à l'insertion"
		            };
		        }
		        callback(reponse);
		    });
		} else{
			let reponse = reponse = {
                    	succes: false,
                        error : err,
                        msg:"Problème lors de l'insertion, erreur de connexion."
                    };
            callback(reponse);
		}
	});
}


exports.login = function(formData, callback) {

MongoClient.connect(url, function(err, client) {
    var db = client.db(dbName);
    if(!err) {

        let myquery = {"username": formData.username,"password": formData.password};
		 
		const user={
			username:formData.username,
			password:formData.password
		}
		
        db.collection("utilisateur") 
        .findOne(myquery, function(err, data) {
            let reponse;
            if(!err){	
                reponse = {     
								username:data.username,
								access_token:jwt.sign({user}, 'secretkey')

								};
            } else{
                reponse = {
                    succes: false,
                    plugin : null,
                    error : err,
                    msg: "erreur lors du find"

                };
            }
            callback(reponse);
        });
    } else {
        let reponse = reponse = {
                    succes: false,
                    plugin : null,
                    error : err,
                    msg: "erreur de connexion à la base"
                };
        callback(reponse);
    }
});
}
console.log("oui")
*/