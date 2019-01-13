var MongoClient = require('mongodb').MongoClient;
var ObjectId = require('mongodb').ObjectID;

var assert = require('assert');
//var url = 'mongodb://localhost:27017/test';

// Connection URL
const url = 'mongodb://localhost:27017';

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


exports.sendMessage = function(formData, callback) {
	MongoClient.connect(url, function(err, client) {
		var db = client.db(dbName);

	    if(!err) {
	
            let toInsert = {
                author : "nadir",
                message : "nadir", 
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