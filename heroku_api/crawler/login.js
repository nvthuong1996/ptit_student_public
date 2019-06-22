const request = require("request");
const cheerio = require("cheerio");
const fs = require("fs");

const HEADER = require("../otherData/header.json");

var login = (user, pass, session) => {
  if (session) {
    return requestLogin(null, null, null, session);
  }
  return requestHome()
    .then(data => {
      return changeLanguage(data);
    })
    .then(data => {
      return checkCapcha(data);
    })
    .then(data => {
      return requestLogin(user, pass, data);
    });
};

module.exports = login;

function requestLogin(user, pass, data, session) {
  return new Promise((resolve, reject) => {
    let option = null;
    let cookie = null;
    if (session) {
      const header = JSON.parse(JSON.stringify(HEADER));
      header["Cookie"] = session.trim();
      cookie = header["Cookie"];
      option = {
        method: "GET",
        uri: "http://qldt.ptit.edu.vn/default.aspx",
        headers: header,
        followAllRedirects: true
      };
    } else {
      cookie = data.cookie;
      const form = data.form;
      form["ctl00$ContentPlaceHolder1$ctl00$ucDangNhap$txtTaiKhoa"] = user;
      form["ctl00$ContentPlaceHolder1$ctl00$ucDangNhap$txtMatKhau"] = pass;
      form["ctl00$ContentPlaceHolder1$ctl00$ucDangNhap$btnDangNhap"] =
        "Đăng nhập";

      const header = JSON.parse(JSON.stringify(HEADER));
      header["Cookie"] = cookie;
      header["Content-Type"] = "application/x-www-form-urlencoded";
      option = {
        method: "POST",
        uri: "http://qldt.ptit.edu.vn/default.aspx",
        headers: header,
        form: form,
        followAllRedirects: true
      };
    }

    request(option, (err, res, body) => {
      if (err || res.statusCode !== 200) {
        return reject(new Error("Lỗi khi thực hiện login. Vui lòng thử lại"));
      }

      const infoUser = {};

      const $ = cheerio.load(body);

      const _user =
        $("#ctl00_Header1_ucLogout_lblNguoiDung")
          .text()
          .trim() ||
        $("#ctl00_Header1_Logout1_lblNguoiDung")
          .text()
          .trim();
      if (!_user) {
        return reject(new Error("Không thể đăng nhập!"));
      }

      if (_user.toLowerCase().indexOf("chào quản trị viên") >= 0) {
        infoUser["type"] = "admin";
      } else {
        var regex = new RegExp(`Chào bạn(.*?)\\)`, "g");
        var regex2 = new RegExp(`Chào giảng viên(.*?)\\)`, "g");
        
        var match1 = regex.exec(_user);
        var match2 = regex2.exec(_user);
        var match = match1 || match2
        if (match) {
          infoUser["name"] = match[1]
            .trim()
            .split("(")[0]
            .trim();
          infoUser["mssv"] = match[1].trim().split("(")[1];
        } else {
          return reject(new Error("Error syntax regex. Không match được mssv"));
        }

        if (body.indexOf("ctl00_menu_lblDangKyMonHoc") < 0) {
          infoUser["type"] = "gv";
        } else {
          infoUser["type"] = "sv";
        }
      }
      return resolve({
        user: infoUser,
        cookie: cookie
      });
    });
  });
}

function checkCapcha(data) {
  const $ = cheerio.load(data.body);
  const captchaElement = $("#ctl00_ContentPlaceHolder1_ctl00_lblCapcha");
  const capcha = $(captchaElement).text();
  if (capcha) {
    return new Promise((resolve, reject) => {
      let option = null;
      let cookie = null;

      cookie = data.cookie;
      const form = data.form;
      form["ctl00$ContentPlaceHolder1$ctl00$txtCaptcha"] = capcha;
      form["ctl00$ContentPlaceHolder1$ctl00$btnXacNhan"] =
        "Vào website";

      const header = JSON.parse(JSON.stringify(HEADER));
      header["Cookie"] = cookie;
      header["Content-Type"] = "application/x-www-form-urlencoded";
      option = {
        method: "POST",
        uri: "http://qldt.ptit.edu.vn/default.aspx",
        headers: header,
        form: form,
        followAllRedirects: true
      };

      request(option, (err, res, body) => {
        if (err || res.statusCode !== 200) {
          return reject(new Error("Lỗi khi thực hiện pass captcha. Vui lòng thử lại"));
        }

        const $ = cheerio.load(body);
        const captchaElement = $("#ctl00_ContentPlaceHolder1_ctl00_lblCapcha");
        const capcha = $(captchaElement).text();
        if(capcha){
          return reject(new Error("Lỗi khi thực hiện pass captcha. Vui lòng thử lại"));
        }else{
          var state = /__VIEWSTATE.+?value="(.+?)"/.exec(body);
          if (state) {
            form['__VIEWSTATE'] = state[1];
          }
          var gen = /__VIEWSTATEGENERATOR.+?value="(.+?)"/.exec(body);
          if (gen) {
            form['__VIEWSTATEGENERATOR'] = gen[1];
          }
          return resolve(data);
        }
      });
    });
  }else{
    return data;
  }
}

function requestHome(cookie) {
  return new Promise((resolve, reject) => {
    request(
      {
        method: "GET",
        uri: "http://qldt.ptit.edu.vn/",
        headers: HEADER
      },
      (err, res, body) => {
        if (err || res.statusCode !== 200) {
          return reject(new Error("Lỗi khi request Home"));
        }
        var _cookie = "";
        if (res.headers["set-cookie"]) {
          let i = 0;
          res.headers["set-cookie"].map(e => {
            i++;
            _cookie += e.split(";")[0];
            if (i < res.headers["set-cookie"].length) {
              _cookie += "; ";
            }
          });
        } else {
          return reject(new Error("Không có cookie"));
        }
        var state = /__VIEWSTATE.+?value="(.+?)"/.exec(body);
        if (state) {
          state = state[1];
        }
        var gen = /__VIEWSTATEGENERATOR.+?value="(.+?)"/.exec(body);
        if (gen) {
          gen = gen[1];
        }
        var form = {
          __EVENTTARGET: "",
          __EVENTARGUMENT: "",
          __VIEWSTATE: state,
          __VIEWSTATEGENERATOR: gen
        };
        return resolve({
          cookie: _cookie,
          form: form,
          body: body
        });
      }
    );
  });
}

async function changeLanguage(data) {
  return new Promise((resolve, reject) => {
    const header = JSON.parse(JSON.stringify(HEADER));
    header["Content-Type"] = "text/plain; charset=UTF-8";
    header["Content-Length"] = 13;
    header["X-AjaxPro-Method"] = "ChangeLanguage";
    header["Cookie"] = data.cookie;
    request(
      {
        uri:
          "http://qldt.ptit.edu.vn/ajaxpro/EduSoft.Web.UC.Logout,EduSoft.Web.ashx",
        method: "POST",
        headers: header,
        body: `{"lan":"_vi"}`
      },
      (error, res, body) => {
        try {
          const _tmp = JSON.parse(body);
          if (_tmp.value === "") {
            return resolve(data);
          }
          return resolve();
        } catch (error) {
          console.log("error change language");
          return resolve();
        }
      }
    );
  });
}
