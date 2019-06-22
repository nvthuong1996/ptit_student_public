const request = require("request");
const fs = require("fs");
const md5 = require("md5");
const mysql = require("../mysql");
const { db } = require("../firebase");


module.exports = updateCSDL;

async function updateCSDL(data) {
  const task = [];
  const connectSQL = mysql();

  if (data.basicInfo) {
    //update basicinfo
    task.push(updateRealtimeDb(data, db,connectSQL));
    task.push(updateBasicInfo(data.basicInfo,connectSQL))
  }
  if (data.diem) {
    task.push(updateMark(data.diem, data.basicInfo.mssv, connectSQL));
  }

  const [t1, t2, t3] = await Promise.all(task);
  if (t2.error || t3.error) {
    return {
      success: false,
      error: t2.error || t3.error
    };
  } else {
    return {
      success: true
    };
  }
}
async function updateBasicInfo(basicInfo, connect) {
  try {
    await new Promise((resolve, reject) => {
      connect.query(
        `CREATE TABLE \`ptitchatbox\`.\`qldt_account\` (
        \`mssv\` VARCHAR(50) NOT NULL,
        \`id\` VARCHAR(50) NOT NULL,
        \`ten_sv\` VARCHAR(70) NULL,
        \`gioi_tinh\` VARCHAR(10) NULL,
        \`ngay_sinh\` VARCHAR(20) NULL,
        \`noi_sinh\` VARCHAR(100) NULL,
        \`lop\` VARCHAR(100) NULL,
        \`nganh\` VARCHAR(100) NULL,
        \`khoa\` VARCHAR(100) NULL,
        \`he_dao_tao\` VARCHAR(100) NULL,
        \`khoa_hoc\` VARCHAR(100) NULL,
      PRIMARY KEY (\`id\`));`,
        (err, r) => {
          if (err && err.code !== "ER_TABLE_EXISTS_ERROR") {
            console.log(err);
          }
          return resolve();
        }
      );
    });

    const sql = `SELECT mssv FROM qldt_account WHERE id="${basicInfo.id}"`;
    await new Promise((resolve, reject) => {
      connect.query(sql, (err, result) => {
        if (err) {
          return console.log(err);
        }
        if (result.length === 0) {
          const sql = `INSERT INTO qldt_account (gioi_tinh,he_dao_tao,mssv,khoa,khoa_hoc,lop,nganh,ngay_sinh,noi_sinh,ten_sv,id)
          VALUE  ("${basicInfo.gioi_tinh}",
            "${basicInfo.he_dao_tao}",
            "${basicInfo.mssv}",
            "${basicInfo.khoa}",
            "${basicInfo.khoa_hoc}",
            "${basicInfo.lop}",
            "${basicInfo.nganh}",
            "${basicInfo.ngay_sinh}",
            "${basicInfo.noi_sinh}",
            "${basicInfo.ten_sv}",
            "${basicInfo.id}"
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
            ten_sv="${basicInfo.ten_sv}",
            mssv="${basicInfo.mssv}"
            WHERE id="${basicInfo.id}"`;
          connect.query(sql, (err, r2) => {
            if (err) {
              console.log(err);
            }
            return resolve();
          });
        }
      });
    });

    return {success:true}
  } catch (ex) {
    return {error:ex};
  }
}

async function updateMark(diem, mssv, connect) {
  try {
    let bangdiem = [];
    const task = [];
    for (let i in diem) {
      task.push(
        updateDiem(i, diem[i], mssv, connect).then(result => {
          if (result.namhoc) {
            return bangdiem.push(result);
          }else if(result.error){
              //throw result.error
          }
          return null;
        })
      );
    }
    await Promise.all(task);

    return { error: null };
  } catch (ex) {
    return { error: ex };
  }
}

async function updateDiem(namhoc, diem, mssv, connect) {
  if (diem[0][diem[0].length - 1].trim() === "") {
    return {error:new Error("invalid value")};
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
        if (err && err.code !== "ER_TABLE_EXISTS_ERROR") {
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
    return {error:ex};
  }
  return {namhoc};
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

async function updateRealtimeDb(data, db,connectSQL) {
  try {
    const uid = data.uid;
    const basicInfo = data.basicInfo;
    const id = basicInfo.id;
    if(!uid){
      return;
    }

    
    // const ref = db.ref("checkqldtaccount/" + id);
    // await ref.update({...basicInfo,uid:uid});
    // const task = [];


    const ref = db.ref("users/" + id);
    await ref.update({...basicInfo,uid:md5(id+"PTITCHATBOX")});
    const task = [];

    task.push(accessQLDT(basicInfo.mssv,connectSQL));

    if(uid){
      task.push(db.ref("linkid/"+uid).update({
        public:id,
        private:md5(id+"PTITCHATBOX")
      }));
    }

    if(basicInfo.idforapp){
      task.push(db.ref("linkid/"+basicInfo.idforapp).update({
        public:id,
        private:md5(id+"PTITCHATBOX")
      }));
    }

    if(basicInfo.idforpage){
      task.push(db.ref("linkid/"+basicInfo.idforpage).update({
        public:id,
        private:md5(id+"PTITCHATBOX")
      }));
    }

    if (data.diem) {
      task.push(
        ref.update({
          bangdiem: data.diem
        })
      );
    }
    if (data.dkmh&&!data.dkmh.error) {
      task.push(
        ref.update({
          dkmh: data.dkmh
        })
      );
    }
    if(data.lichthi&&!data.lichthi.error){
      task.push(
        ref.update({
          lichthi: data.lichthi
        })
      );
    }
    await Promise.all(task);
    return { error: null };
  } catch (error) {
    return { error };
  }
}

function accessQLDT(mssv,connectSQL){
  return new Promise((resolve,reject)=>{
    connectSQL.query(`REPLACE  \`qldtoverload\`.\`access\` SET \`mssv\`='${mssv}',\`access\`='1'`,(error,result)=>{
      resolve();
    })
  })
}
