var mysql = require('mysql');

var con = mysql.createConnection({
  host: "82.255.166.104",
  user: "GrailsUser",
  password: "GrailsPassword13?"
});

con.connect(function(err) {
  if (err) throw err;
  console.log("Connected!");
});