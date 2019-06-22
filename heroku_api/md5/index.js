const md5 = require('md5');

module.exports = function (key){
    return md5(key+'PTITCHATBOX');
}