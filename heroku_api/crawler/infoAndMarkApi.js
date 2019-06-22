const request = require("request");
const fs = require("fs");
const cheerio = require("cheerio");
const HEADER = JSON.parse(fs.readFileSync("./otherData/header.json", "utf8"));
const HASH = require("../md5");
const {db} = require("../firebase");

module.exports = async function(cookie) {
  let basic = null;
  try {
    basic = await getBasicInfo(cookie);
  } catch (error) {
    throw error;
  }

  try {
    let { diem } = await getDiem(basic.next);
    return { diem, baseinfo: basic.baseinfo };
  } catch (error) {
    //handle error
    db.ref("error/"+basic.baseinfo.mssv).update({
      diemthi:{
        body:error.body
      }
    })
    return { baseinfo: basic.baseinfo };
  }
};

function getBasicInfo(cookie) {
  return new Promise((resolve, reject) => {
    var header = JSON.parse(JSON.stringify(HEADER));
    header["Cookie"] = cookie;
    request(
      {
        method: "GET",
        uri: "http://qldt.ptit.edu.vn/Default.aspx?page=xemdiemthi",
        headers: header
      },
      (err, res, body) => {
        if (err || res.statusCode !== 200) {
          return reject(new Error("Có lỗi khi crawler thông tin cơ bản"));
        }

        var type;
        if (body.indexOf("hập mã số cần xem:") > 0) {
          if (body.indexOf("Chào Quản Trị Viên") > 0) {
            type = "admin";
          } else {
            type = "gv";
          }
          return reject(new Error("Không crawler của gv hoặc admin"));
        } else {
          type = "sv";

          const $ = cheerio.load(body);

          const _user =
            $("#ctl00_Header1_ucLogout_lblNguoiDung")
              .text()
              .trim() ||
            $("#ctl00_Header1_Logout1_lblNguoiDung")
              .text()
              .trim();
          if (!_user) {
            return reject(
              new Error("Chưa đăng nhập khi crawler thông tin cơ bản")
            );
          }

          var mssv = $(
            "#ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lblMaSinhVien"
          ).text();

          if (!mssv || mssv.length < 5) {
            return reject(new Error("Có lỗi khi crawler basic info!"));
          }

          var ten_sv = $(
            "#ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lblTenSinhVien"
          ).text();
          var gioi_tinh = $(
            "#ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lblPhai"
          ).text();
          var ngay_sinh = $(
            "#ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lblNgaySinh"
          ).text();
          var noi_sinh = $(
            "#ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lblNoiSinh"
          ).text();
          var lop = $(
            "#ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lblLop"
          ).text();
          var nganh = $(
            "#ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lbNganh"
          ).text();
          var khoa = $(
            "#ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lblKhoa"
          ).text();
          var he_dao_tao = $(
            "#ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lblHeDaoTao"
          ).text();
          var khoa_hoc = $(
            "#ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lblKhoaHoc"
          ).text();

          var form = getForm(
            [
              "__EVENTTARGET",
              "__EVENTARGUMENT",
              "__VIEWSTATE",
              "__VIEWSTATEGENERATOR"
            ],
            body
          );
          form["ctl00$ContentPlaceHolder1$ctl00$txtChonHK"] = "";
          form["__EVENTTARGET"] =
            "ctl00$ContentPlaceHolder1$ctl00$lnkChangeview2";

          return resolve({
            baseinfo: {
              id: HASH(mssv),
              mssv: mssv,
              ten_sv: ten_sv,
              gioi_tinh: gioi_tinh,
              ngay_sinh: ngay_sinh,
              noi_sinh: noi_sinh,
              lop: lop,
              nganh: nganh,
              he_dao_tao: he_dao_tao,
              khoa_hoc: khoa_hoc,
              khoa: khoa,
              type: type
            },
            next: {
              cookie: cookie,
              form: form
            }
          });
        }
      }
    );
  });
}

function getDiem(data) {
  const form = data.form;
  cookie = data.cookie;
  var header = JSON.parse(JSON.stringify(HEADER));
  header["Cookie"] = cookie;
  header["Content-Type"] = "application/x-www-form-urlencoded";

  return new Promise((resolve, reject) => {
    request(
      {
        method: "POST",
        uri: "http://qldt.ptit.edu.vn/Default.aspx?page=xemdiemthi",
        headers: header,
        form: form,
        followAllRedirects: true
      },
      (err, res, body) => {
        if (err || res.statusCode !== 200) {
          return reject(new Error("Lỗi request khi crawler điểm"));
        }

        /*const $ = cheerio.load(body);
  
          const _diem = $("#ctl00_ContentPlaceHolder1_ctl00_div1 table tbody");
          const r = [];
  
          const _q = _diem.children();
          const result = {};
          try {
            for (let i = 0; i < _q.length; i++) {
              const _p = _q[i].children;
              r[r.length] = [];
              for (let j = 0; j < _p.length; j++) {
                const _t = _p[j];
                if (_t.name != "td") continue;
                r[r.length - 1] = r[r.length - 1].concat($(_t).text());
              }
              if (r[r.length - 1].length < 10) {
                const _t = r[r.length - 1][0].toLowerCase();
                if (_t.indexOf("năm học") >= 0) {
                  var hocky = _t.replace(/[^0-9|-]/g, "");
                  result[hocky] = [];
                } else {
                  var hocky = null;
                }
              } else if (hocky) {
                result[hocky].push(r[r.length - 1]);
              }
            }
          } catch (ex) {
            return reject(ex);
          }*/
        const result = crawlerQLDT(body);
        if (result.error) {
          return reject(result);
        }

        return resolve({
          diem: result
        });
      }
    );
  });
}

function crawlerQLDT(body) {
  const $ = cheerio.load(body);

  const _user =
    $("#ctl00_Header1_ucLogout_lblNguoiDung")
      .text()
      .trim() ||
    $("#ctl00_Header1_Logout1_lblNguoiDung")
      .text()
      .trim();
  if (!_user) {
    return reject(new Error("Chưa đăng nhập khi crawler thông tin cơ bản"));
  }

  const diem = $("#ctl00_ContentPlaceHolder1_ctl00_div1 table tbody");

  const arrTrTag = diem[0].children.filter(
    item => item.type === "tag" && item.name === "tr"
  );

  const result = [];
  const result2 = [];

  let tongkethk = {};
  for (let i = 1; i < arrTrTag.length - 1; i++) {
    let namhoc = "";
    const _test = $(arrTrTag[i])
    .text()
    .trim();
    if (arrTrTag[i + 1].children.length === 19) {
      const _tmp = $(arrTrTag[i])
        .text()
        .trim();
      if (_tmp === "Điểm bảo lưu") {
        namhoc = "DIEM-BAO-LUU";
      } else {
        namhoc = _tmp.replace(",", "-").replace(/[^0-9|-]/g, "");
        if (namhoc.length !== 11) {
          return { error: new Error("Sai thuật toán crawler điểm"), body };
        }
      }
    } else {
      const _tmp = arrTrTag[i + 1].children.filter(item=>item.type=='tag');
      if(_tmp.length == 1){
        const child = _tmp[0];
        const filter = child.children.filter(item=>item.type=="tag");
        const key = $(filter[0]).text().trim();
        const value = $(filter[1]).text().trim();
        if(key == "Điểm trung bình học kỳ hệ 10/100:"){
          tongkethk['tbhkhe10100'] = value;
        }else if(key == "Điểm trung bình học kỳ hệ 4:"){
          tongkethk['tbhkhe4'] = value;
        }else if(key == "Điểm trung bình tích lũy:"){
          tongkethk['tbtichluy'] = value;
        }else if(key == "Điểm trung bình tích lũy (hệ 4):"){
          tongkethk['tbtichluyhe4'] = value;
        }else if(key == "Số tín chỉ đạt:"){
          tongkethk['tinchidat'] = value;
        }else if(key == "Số tín chỉ tích lũy:"){
          tongkethk['tinchitichluy'] = value;
          result[result.length - 1]['tk'] = tongkethk;
          tongkethk = {};
        }
      }
      continue;
    }
    i++;
    const mh = [];
    if (arrTrTag[i].children.length < 15) {
      return { error: new Error("Sai thuật toán crawler điểm"), body };
    }
    while (arrTrTag[i].children.length > 15) {
      const tmp = arrTrTag[i].children
        .filter(f => f.type === "tag" && f.name === "td")
        .map(e => {
          const value = $(e)
            .text()
            .trim();
          return value;
        });
      mh.push(tmp);
      i++;
      if (i === arrTrTag.length) {
        const find = result.find((item)=>item.hk == namhoc);
        if(!find){
          result.push({
            hk:namhoc,
            data:mh,
          });
        }else{
          find.data = mh;
        }
        result2[namhoc] = mh;
        break;
      }
    }
    i--;
    result2[namhoc] = mh;

    const find = result.find((item)=>item.hk == namhoc);
    if(!find){
      result.push({
        hk:namhoc,
        data:mh,
      });
    }else{
      find.data = mh;
    }


  }
  return [result,result2];
}

function matchIDtagSpan(id, body) {
  var regex = new RegExp(`<([^\\s]+).*?id="${id}".*?>(.+?)</span>`);
  var match = regex.exec(body);
  if (match) {
    return match[2];
  } else {
    return null;
  }
}

function getForm(arg, data) {
  const form = {};
  arg.forEach(element => {
    const regex = new RegExp(`${element}.+?value="(.+?)"`);
    const match = regex.exec(data);
    if (match) {
      form[element] = match[1];
    } else {
      form[element] = "";
    }
  });
  return form;
}
