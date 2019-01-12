let http =require('http')
let express  = require('express');

var router=express.Router();
var User =require('./User');

let server =http.createServer()

router.get('/',function(req,res,next){
    res.render('index',{titlt:'Express'});
});

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

module.exports=router;

server.listen(8888)