var express = require('express')
var app = express()

app.set('port', (process.env.PORT || 5000))
app.use(express.static(__dirname + '/public'))

app.get('/', function(request, response) {
  response.send('Hello World!')
})




app.listen(app.get('port'), function() {
  console.log("Node app is running at localhost:" + app.get('port'))
})



router.post('/login', function(req, res){
    var username=req.body.username;
    var password=req.body.password;
 
    User.findOne({username:username,password:password},function(err,user){
        if(err){
            console.log(err);
            return res.status(500).send();
        }
        if(!user){
            return res.status(404).send();
        }
        return res.status(200).send();
    
    })
 })
 
 
 router.post('/register', function(req,res){
     var username=req.body.username;
     var password=req.body.password;
     var firstname=req.body.firstname;
     var lastname=req.body.lastname;
      
     var newUser =new User();
     newUser.username=username;
     newUser.password=password;
     newUser.firstname=firstname;
     newUser.lastname=lastname;
     newUser.save(function(err,saveUser){
         if(err){
             console.log(err);
             return res.status(500).send();
         }
 
         return res.status(200).send();
     })
 })
 