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

