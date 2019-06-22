const request = require("request");
const fs = require("fs");
const cheerio = require("cheerio");
const { firestore } = require("../firebase");
const md5 = require("md5");

const mysql = require("../mysql");

const HEADER = require("../otherData/header.json");

module.exports = crawlerFull;

async function crawlerFull(cookie) {
  try {
    var basic = await getBasicInfo(cookie);
    if (!basic.baseinfo.id) {
      console.log("Co loi khi crawler");
      return;
    }
    var { diem } = await getDiem(basic.next);
    if (!diem)
      throw `Crawler mssv ${
        basic.baseinfo.id
      } that bai. Co the do sai thuat toan`;
  } catch (ex) {
    console.log(ex);
    return String(ex);
  }

  if (basic.baseinfo.type === "sv") {
    const connect = mysql();
    try {
      const r = await updateDatabase(
        {
          basicInfo: basic.baseinfo,
          diem: diem
        },
        connect
      );
      if (r.err) {
        throw r.err;
      }
    } catch (ex) {
      return String(ex);
    }
    connect.end();
    return basic.baseinfo.id;
  }
}

/*const data = JSON.parse(fs.readFileSync("./test.json", "utf8"));

updateDatabase(data);*/

async function updateDatabase({ basicInfo, diem }, connect) {
  try {
    await updateBasicInfo(basicInfo, connect);
    let bangdiem = [];
    for (let i in diem) {
      try {
        const _tmp = await updateDiem(i, diem[i], basicInfo.id, connect);
        if (_tmp) {
          bangdiem.push(i);
        }
      } catch (ex) {
        console.log(ex);
      }
    }
    await firestore
      .collection("users")
      .doc(md5(basicInfo.id + "PTITCHATBOX"))
      .update({
        bangdiem: bangdiem
      });

    return { err: null };
  } catch (ex) {
    return { err: String(ex) };
  }
}

async function updateDiem(namhoc, diem, mssv, connect) {
  if (diem[0][diem[0].length - 1].trim() == "") {
    return false;
  }
  await new Promise((resolve, reject) => {
    connect.query(
      `CREATE TABLE \`ptitchatbox\`.\`${namhoc}\` (
		    \`ID\` int NOT NULL AUTO_INCREMENT,
			\`mssv\` VARCHAR(20) NULL,
			\`maMH\` VARCHAR(20) NULL,
			\`tenMH\` VARCHAR(200) NULL,
      \`TC\` FLOAT NULL,
			\`%CC\` FLOAT NULL,
			\`%KT\` FLOAT NULL,
			\`%TH\` FLOAT NULL,
			\`%BT\` FLOAT NULL,
			\`%THI\` FLOAT NULL,
			\`CC\` FLOAT NULL,
			\`KT\` FLOAT NULL,
			\`TH\` FLOAT NULL,
			\`BT\` FLOAT NULL,
			\`thiL1\` FLOAT NULL,
			\`thiL2\` FLOAT NULL,
			\`TK10\` FLOAT NULL,
			\`TKCH\` VARCHAR(2) NULL,
			PRIMARY KEY (\`id\`));`,
      (err, r) => {
        if (err && err.code != "ER_TABLE_EXISTS_ERROR") {
          console.log(err);
        }
        return resolve();
      }
    );
  });

  const task = [];
  for (let i = 0; i < diem.length; i++) {
    task.push(updateMotMonHoc(mssv, diem[i], namhoc, connect));
  }
  try {
    await Promise.all(task);
  } catch (ex) {
    console.log(ex);
    return false;
  }
  return true;
}

function updateMotMonHoc(mssv, diem, namhoc, connect) {
  const sql = `SELECT * FROM \`${namhoc}\` WHERE mssv="${mssv}" AND maMH ="${
    diem[1]
  }"`;
  return new Promise((resolve, reject) => {
    connect.query(sql, (err, r) => {
      if (r.length === 0) {
        //Insert
        const sql = `INSERT INTO \`${namhoc}\` (\`mssv\`, \`maMH\`, \`tenMH\`, \`TC\`, \`%CC\`, \`%KT\`, \`%TH\`, \`%BT\`, \`%THI\`, \`CC\`, \`KT\`, \`TH\`, \`BT\`, \`thiL1\`, \`thiL2\`, \`TK10\`, \`TKCH\`)
					VALUE (
					"${mssv}",
					"${diem[1]}",
					"${diem[2]}",
          ${parseValue(diem[3])},
					${parseValue(diem[4])},
					${parseValue(diem[5])},
					${parseValue(diem[6])},
					${parseValue(diem[7])},
					${parseValue(diem[8])},
					${parseValue(diem[9])},
					${parseValue(diem[10])},
					${parseValue(diem[11])},
					${parseValue(diem[12])},
					${parseValue(diem[13])},
					${parseValue(diem[14])},
					${parseValue(diem[15])},
					"${diem[16]}");
					`;
        connect.query(sql, (err, r) => {
          if (err) {
            console.log(err);
          }
          return resolve();
        });
      } else {
        //Update
        const sql = `UPDATE \`${namhoc}\` SET
						\`tenMH\`="${diem[2]}",
            \`TC\`=${parseValue(diem[3])},
						\`%CC\`=${parseValue(diem[4])},
						\`%KT\`=${parseValue(diem[5])},
						\`%TH\`=${parseValue(diem[6])},
						\`%BT\`=${parseValue(diem[7])},
						\`%THI\`=${parseValue(diem[8])},
						\`CC\`=${parseValue(diem[9])},
						\`KT\`=${parseValue(diem[10])},
						\`TH\`=${parseValue(diem[11])},
						\`BT\`=${parseValue(diem[12])},
						\`thiL1\`=${parseValue(diem[13])},
						\`thiL2\`=${parseValue(diem[14])},
						\`TK10\`=${parseValue(diem[15])},
						\`TKCH\`="${diem[16]}"
						WHERE mssv="${mssv}" AND maMH="${diem[1]}"
					`;
        connect.query(sql, (err, r) => {
          if (err) {
            console.log(err);
          }
          return resolve();
        });
      }
    });
  });
}

function parseValue(value) {
  try {
    const _t = parseFloat(value);
    if (isNaN(_t)) return "NULL";
    return `"${_t}"`;
  } catch (ex) {
    return "NULL";
  }
}

async function updateBasicInfo(basicInfo, connect) {
  try {
    await new Promise((resolve, reject) => {
      connect.query(
        `CREATE TABLE \`ptitchatbox\`.\`qldt_account\` (
        \`mssv\` VARCHAR(50) NOT NULL,
        \`ten_sv\` VARCHAR(70) NULL,
        \`gioi_tinh\` VARCHAR(10) NULL,
        \`ngay_sinh\` VARCHAR(20) NULL,
        \`noi_sinh\` VARCHAR(100) NULL,
        \`lop\` VARCHAR(100) NULL,
        \`nganh\` VARCHAR(100) NULL,
        \`khoa\` VARCHAR(100) NULL,
        \`he_dao_tao\` VARCHAR(100) NULL,
        \`khoa_hoc\` VARCHAR(100) NULL,
      PRIMARY KEY (\`mssv\`));`,
        (err, r) => {
          if (err && err.code != "ER_TABLE_EXISTS_ERROR") {
            console.log(err);
          }
          return resolve();
        }
      );
    });

    const sql = `SELECT mssv FROM qldt_account WHERE mssv="${basicInfo.id}"`;
    await new Promise((resolve, reject) => {
      connect.query(sql, (err, result) => {
        if (err) {
          return console.log(err);
        }
        if (result.length == 0) {
          const sql = `INSERT INTO qldt_account (gioi_tinh,he_dao_tao,mssv,khoa,khoa_hoc,lop,nganh,ngay_sinh,noi_sinh,ten_sv)
          VALUE  ("${basicInfo.gioi_tinh}",
            "${basicInfo.he_dao_tao}",
            "${basicInfo.id}",
            "${basicInfo.khoa}",
            "${basicInfo.khoa_hoc}",
            "${basicInfo.lop}",
            "${basicInfo.nganh}",
            "${basicInfo.ngay_sinh}",
            "${basicInfo.noi_sinh}",
            "${basicInfo.ten_sv}"
          )`;
          connect.query(sql, (err, r2) => {
            if (err) {
              console.log(err);
            }
            return resolve();
          });
        } else {
          const sql = `UPDATE qldt_account SET
            gioi_tinh="${basicInfo.gioi_tinh}",
            he_dao_tao="${basicInfo.he_dao_tao}",
            khoa="${basicInfo.khoa}",
            khoa_hoc="${basicInfo.khoa_hoc}",
            lop="${basicInfo.lop}",
            nganh="${basicInfo.nganh}",
            ngay_sinh="${basicInfo.ngay_sinh}",
            noi_sinh="${basicInfo.noi_sinh}",
            ten_sv="${basicInfo.ten_sv}"
            WHERE mssv="${basicInfo.id}"`;
          connect.query(sql, (err, r2) => {
            if (err) {
              console.log(err);
            }
            return resolve();
          });
        }
      });
    });
  } catch (ex) {
    console.log(ex);
  }
}

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
          var regex = new RegExp(`Chào(.*?)\\)`, "g");
          var match = regex.exec(body);
          if (!match) {
            return reject(new Error("Lỗi chưa login khi crawler thông tin cơ bản"));
          }

          var mssv = matchIDtagSpan(
            "ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lblMaSinhVien",
            body
          );
          var ten_sv = matchIDtagSpan(
            "ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lblTenSinhVien",
            body
          );
          var gioi_tinh = matchIDtagSpan(
            "ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lblPhai",
            body
          );
          var ngay_sinh = matchIDtagSpan(
            "ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lblNgaySinh",
            body
          );
          var noi_sinh = matchIDtagSpan(
            "ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lblNoiSinh",
            body
          );
          var lop = matchIDtagSpan(
            "ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lblLop",
            body
          );
          var nganh = matchIDtagSpan(
            "ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lbNganh",
            body
          );
          var khoa = matchIDtagSpan(
            "ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lblKhoa",
            body
          );
          var he_dao_tao = matchIDtagSpan(
            "ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lblHeDaoTao",
            body
          );
          var khoa_hoc = matchIDtagSpan(
            "ctl00_ContentPlaceHolder1_ctl00_ucThongTinSV_lblKhoaHoc",
            body
          );

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
              id: mssv,
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
        var regex = new RegExp(`Chào(.*?)\\)`, "g");
        var match = regex.exec(body);
        if (!match) {
          return reject(new Error("Lỗi chưa login khi crawler điểm"));
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

        return resolve({
          diem: result
        });
      }
    );
  });
}

function crawlerQLDT(body) {
  const $ = cheerio.load(body);

  const diem = $("#ctl00_ContentPlaceHolder1_ctl00_div1 table tbody");

  const arrTrTag = diem[0].children.filter(
    item => item.type === "tag" && item.name === "tr"
  );

  const result = {};
  for (let i = 1; i < arrTrTag.length - 1; i++) {
    let namhoc = "";
    if (arrTrTag[i + 1].children.length == 19) {
      namhoc = $(arrTrTag[i])
        .text()
        .replace(/[^0-9|-]/g, "");
      if (namhoc.length !== 11) {
        console.log("Sai thuat toan crawler");
        return null;
      }
    } else {
      continue;
    }
    i++;
    const mh = [];
    if (arrTrTag[i].children.length < 15) {
      console.log("Sai thuat toan crawler");
      return null;
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
        result[namhoc] = mh;
        break;
      }
    }
    result[namhoc] = mh;
  }
  return result;
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
