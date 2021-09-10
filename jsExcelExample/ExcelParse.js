/* --------------------------------------------------------
*
* ExcelParse
*
-----------------------------------------------------------*/
var ExcelParse = function($Control, $obj){
 
	//private
	var name = "ExcelParse";
	var author = "허정진";
  var ver = "2017.06.01";
  
  this.handleFile = this.handleFile.bind(this);
	
	//extend
	this.defaultObj = {
			validFileExtensions : [".xlsx", ".xls", "csv"],
			rABS : true, // T : 바이너리, F : 어레이 버퍼
	};
 
 for(var key in $obj) if( $obj.hasOwnProperty(key) ) this.defaultObj[key] = $obj[key];
 
}//end. ExcelParse

ExcelParse.prototype = (function(){
 
	return{
		constructor: ExcelParse,

		//
		inherit: function( Parent, Child ){
			Child = function(){
					Parent.call( this );
			}

			try{
        if (!Object.create) {
          Object.create = (function(){
            function F(){}
              return function(o){
              if (arguments.length != 1) {
                throw new Error('Object.create implementation only accepts one parameter.');
              }
              F.prototype = o;
              return new F();
            }
          })();
        }
				Child.prototype = Object.create( Parent.prototype );
					Child.prototype.constructor = Child;
					//override
					//Child.prototype.build = function(){ alert('hi, I am a Child'); };
					var child = new Child();
					if(child instanceof Parent === true) return child;
					else return new Parent();
			}catch(e){
					throw new Error( "[ inherit Error ] : "+ Parent.name +"객체를 상속받지 못했습니다. "+ Parent.name +" 객체를 '확인'해 주세요." );
			}
		},
		
		// 어레이 버퍼를 처리한다 ( 오직 readAsArrayBuffer 데이터만 가능하다 )
		fixdata : function(){
				var o = "", l = 0, w = 10240;
				for(; l<data.byteLength/w; ++l) o+=String.fromCharCode.apply(null,new Uint8Array(data.slice(l*w,l*w+w)));
				o+=String.fromCharCode.apply(null, new Uint8Array(data.slice(l*w)));
				return o;
		},

		// 데이터를 바이너리 스트링으로 얻는다.
		getConvertDataToBin : function(){
				var arraybuffer = $data;
				var data = new Uint8Array(arraybuffer);
				var arr = new Array();
				for(var i = 0; i != data.length; ++i) arr[i] = String.fromCharCode(data[i]);
				var bstr = arr.join("");
				return bstr;
		},

	  handleFile : function(e, $callback){
      
      //업로드 될 파일 확장자 검사
      var form = $("form");
      var _this = this;
      
      if( !this.validate( form ) ) return false;
  
      var files = e.target.files;
      var i,f;
      for (i = 0; i != files.length; ++i) {
        f = files[i];
        var reader = new FileReader();
        var name = f.name;

        reader.onload = function(e) {
          var data = e.target.result;
          var workbook;

          if(_this.defaultObj.rABS) {
              /* if binary string, read with type 'binary' */
              workbook = XLSX.read(data, {type: 'binary'});
          } else {
              /* if array buffer, convert to base64 */
              var arr = fixdata(data);
              workbook = XLSX.read(btoa(arr), {type: 'base64'});
          }//end. if

            /* 워크북 처리 */
			    var htmlTable, csvToFSRS;
          workbook.SheetNames.forEach(function(item, index, array) {
            
            // CSV
            //var csv = XLSX.utils.sheet_to_csv(workbook.Sheets[item]); // default : ","

            //console.log(csv);
            //var csvToFS = XLSX.utils.sheet_to_csv(workbook.Sheets[item], {FS:"\t"} ); // "Field Separator" delimiter between fields
            //var csvToFSRS = XLSX.utils.sheet_to_csv(workbook.Sheets[item], {FS:":",RS:"|"} ); // "\n" "Record Separator" delimiter between rows

            // html
            //var html = XLSX.utils.sheet_to_html(workbook.Sheets[item]);
            //var htmlHF = XLSX.utils.sheet_to_html(workbook.Sheets[item], {header:"<html><title='custom'><body><table>", footer:"</table><body></html>"});
            //var htmlTable = XLSX.utils.sheet_to_html(workbook.Sheets[item], {header:"<table border='1'>", footer:"</table>"});

            // json
            var json = XLSX.utils.sheet_to_json(workbook.Sheets[item]);
            console.log(json);

            //formulae
            //var formulae = XLSX.utils.sheet_to_formulae(workbook.Sheets[item]);
            
            //htmlTable = XLSX.utils.sheet_to_html(workbook.Sheets[item], {header:"<table border='1'>", footer:"</table>"});
            //csvToFSRS = XLSX.utils.sheet_to_csv(workbook.Sheets[item], {FS:":",RS:"|"} ); // "\n" "Record Separator" delimiter between rows
            //getCsvToJson( csvToFSRS );
                
          });//end. forEach
        }; //end onload

        if(this.defaultObj.rABS) reader.readAsBinaryString(f);
        else reader.readAsArrayBuffer(f);
      }//end. for
    },

    getCsvToJson : function( $csv ){
      // TODO...
    },


    /******************************************************************************************************************
    *
    *    validate
    *
    ******************************************************************************************************************/
    validate: function(oForm){

      var _validFileExtensions = this.defaultObj.validFileExtensions || [".jpg", ".jpeg", ".bmp", ".gif", ".png"];
      var arrInputs = oForm.find("input");

      for (var i = 0; i < arrInputs.length; i++) {
        var oInput = arrInputs[i];
        if (oInput.type == "file") {
          var sFileName = oInput.value;
          if (sFileName.length > 0) {
            var blnValid = false;
            for (var j = 0; j < _validFileExtensions.length; j++) {
              var sCurExtension = _validFileExtensions[j];
              if (sFileName.substr(sFileName.length - sCurExtension.length, sCurExtension.length).toLowerCase() == sCurExtension.toLowerCase()) {
                blnValid = true;
                break;
              }
            }

            if (!blnValid) {
              alert("경고, " + sFileName + " 는 유효하지않은 파일입니다.\n\n업로드는 다음형식을 지원합니다 : " + _validFileExtensions.join(", "));
              return false;
            }
          }
        }
      }

      return true;
    },
	}
})();