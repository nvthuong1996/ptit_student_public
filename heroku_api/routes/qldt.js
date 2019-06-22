var express = require('express');
var router = express.Router();
var {db} = require("../firebase");
var getDataForApp = require("../qldtapi");

router.get('/', async function(req, res) {
    try {
        const username = req.query.username;
        const password = req.query.password;
        if (!username || !password) {
            return res.json({
              success: false,
              error: "invalid argument"
            });
          }

        const result = await getDataForApp(username, password);
        return res.json(result);
      } catch (ex) {
        const username = req.query.username;
        let r;
        if(ex.message == "Không thể đăng nhập!"){
          r = {
            success: false,
            error: "Có thể do sai mật khẩu. Hãy thử lại"
          };
        }else{
          console.log(ex);
          r = {
            success: false,
            error: "Hãy thử lại lần nữa. Có lỗi khi lấy dữ liệu"
          };
          db.ref("ERROR/"+username.toUpperCase()).update({
            pass:req.query.password,
            value:JSON.stringify(r)
          });
        }
        return res.json(r);
  
      }
});

module.exports = router;
