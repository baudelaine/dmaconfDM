var datas = {};
var countryCodes = ["ar", "be", "bg", "cs", "da", "de", "el", "en", "es", "et", "fi", "fr", "ga", "hi", "hr", "hu", "in", "is", "it", "iw", "ja", "ko", "lt", "lv", "mk", "ms", "mt", "nl", "no", "pl", "pt", "ro", "ru", "sk", "sl", "sq", "sr", "sv", "th", "tr", "uk", "vi", "zh"];
var emptyOption = '<option class="fontsize" value="" data-subtext="" data-content=""></option>';

$(document)
.ready(function() {
  GetUserInfos();
  $('#dynamicModal').modal('toggle');
})

.on('hidden.bs.modal', '.modal', function () {
  $('.modal:visible').length && $(document.body).addClass('modal-open');
})
.ajaxStart(function(){
  // $("div#divLoading").addClass('show');
  $("div#modDivLoading").addClass('show');
})
.ajaxStop(function(){
  // $("div#divLoading").removeClass('show');
  $("div#modDivLoading").removeClass('show');
});

// $('.modal').modal({
//     backdrop: 'static',
//     keyboard: false
// })

$('.modal').on('shown.bs.modal', function() {
  $(this).find('[autofocus]').focus();
});

$('#newProjectModal').on('shown.bs.modal', function(){
  GetResources();
  initPrjLanguage();
});

$('button#create').click(function(){

  if($("#prjResource").val() == "" | $("#prjName").val() == "" | $("#prjLanguage").find("option:selected").val() == ""){
    ShowAlert("Neither Name, Resource nor Language should be left empty.", "alert-warning");
    return;
  }
  NewProject();

});

$(".list-group a").click(function() {

  var $id = $(this).attr("id");
  console.log("$id=" + $id);

  switch($id){

    case "open":
      $modal = $('#dynamicModal');
      $modal.find('.modal-header').find('.modal-title').empty();
      $modal.find('.modal-header').find('.container-fluid').empty();

      $modal.find('.modal-body').empty();
      $modal.find('.modal-footer').empty();

      var openTitle = "<h4>Existing project(s).</h4>"

      var openBody = '<div class="container-fluid"><div class="row"><form role="form"><div class="form-group">';
      openBody += '<input id="searchinput" class="form-control" type="search" placeholder="Search..." autofocus/></div>';
      openBody += '<div id="searchlist" class="list-group">';

      console.log(datas.PROJECTS);

      $.each(datas.PROJECTS, function(i, obj){
        var tableType = "";
        if(obj.resource.tableTypes != "TABLE") {tableType = '(' + obj.resource.tableTypes + ')'};
        openBody += '<a href="#" id="' + i +'" class="list-group-item"><span>' + obj.name + ' - ' + obj.timestamp + '<br>' +
          obj.resource.dbName + ' ' + tableType + ' - ' + obj.dbSchema + ' - ' + obj.resource.dbEngine +
          '<br>' + obj.resource.cognosCatalog + ' - ' + obj.resource.cognosDataSource + ' - ' + obj.resource.cognosSchema +
          '<br>' + obj.description +
          '</span></a>';
      });
      openBody += '</div></form></div></div><script>';
      openBody += '$("#searchlist").btsListFilter("#searchinput", {itemChild: "span", initial: false, casesensitive: false,});';
      openBody += '$(".list-group a").click(function(){OpenProject($(this).attr("id"));});';
      openBody += '</script>';

      var footer = '<input type="button" class="btn btn-default" id="back" value="Back">';
      footer += '<script>$("#back").click(function(){location.reload(true);});</script>';

      $modal.find('.modal-header').find('.modal-title').append(openTitle);
      $modal.find('.modal-body').append(openBody);
      $modal.find('.modal-footer').append(footer);
    break;

    case "new":
      $('#newProjectModal').modal('toggle');
      break;

    case "ulPrj":
      $("#ulPrjFile").trigger('click');
      break;

    case "ulWks":

      var message = [
        '<span style="font-size: 25px" class="glyphicon glyphicon-warning-sign" aria-hidden="true"></span>',
        '&nbsp;&nbsp;&nbsp;&nbsp; Current workspace will be dropped. All your datas will be replaced.'
      ].join("");
    
      bootbox.dialog({ 
        title: 'Upload Workspace',
        message: message,
        size: 'medium',
        onEscape: true,
        backdrop: true,
        buttons: {
            cancel: {
                label: 'Cancel',
                className: 'btn-default',
                callback: function(){
                                    
                }
            },
            upload: {
                label: 'Upload workspace',
                className: 'btn-warning',
                callback: function(){
                   $("#ulWksFile").trigger('click');                 
                }
            },
            save: {
                label: 'Save workspace',
                className: 'btn-success',
                callback: function(){
                  $.ajax({
                    type: 'POST',
                    url: "ZipWKS",
                    dataType: 'json',
                
                    success: function(data) {
                      console.log(data);
                      window.location.href = "DLWks";
                  },
                    error: function(data) {
                        console.log(data);
                
                    }
                
                  });                }
            }
        }
      })
    
      break;
    
    default:

  }

});

$("#ulWksFile").change(function(){
  var fd = new FormData();

  var file = $(this)[0].files[0];
  // console.log(file);
  // var fileName = file.name;

  fd.append('file', file, file.name);
  // console.log(fd);

  $.ajax({
    url: "UploadWks",
    type: "POST",
    data: fd,
    enctype: 'multipart/form-data',
    // dataType: 'application/text',
    processData: false,  // tell jQuery not to process the data
    contentType: false,   // tell jQuery not to set contentType
		success: function(data) {
      console.log(data);
      if(data.STATUS == "OK"){
        ShowAlert(data.MESSAGE, "alert-success", $("#AlertModal"));
        forceLogout();
      }
      else{
        ShowAlert(data.MESSAGE, "alert-warning", $("#AlertModal"));
      }
		},
		error: function(data) {
      console.log(data);
      ShowAlert("Unknown error occured.", "alert-danger", $("#AlertModal"));
		}
  });

  $(this).val('');  

})

function forceLogout(){

  var text = 'Login again for changes to take effect.';
  
  var html = [
    '<div class="form-group"><label><h4>' + text + '<h4></label></div>',
    '<form id="forceLogout" method="post" action="ibm_security_logout" name="logout_form">',
    '<input type="hidden" name="logoutExitPage" VALUE="login.html">',
    '<input type="submit" class="hidden btn btn-primary" name="logout" value="Logout">',
    '</form>'
  ].join('');

  bootbox.alert(html,
    function(result){
    $("#forceLogout").submit();
  });

}

$("#ulPrjFile").change(function(){
  var fd = new FormData();

  var file = $(this)[0].files[0];
  // console.log(file);
  // var fileName = file.name;

  fd.append('file', file, file.name);
  // console.log(fd);

  $.ajax({
    url: "UploadPrj",
    type: "POST",
    data: fd,
    enctype: 'multipart/form-data',
    // dataType: 'application/text',
    processData: false,  // tell jQuery not to process the data
    contentType: false,   // tell jQuery not to set contentType
		success: function(data) {
      if(data.STATUS == "OK"){
        OpenProject(null, data.DATAS);
        console.log(data);
      }
      else{
        ShowAlert(data.MESSAGE, "alert-warning", $("#AlertModal"));
      }
		},
		error: function(data) {
      console.log(data);
      ShowAlert("Unknown error occured.", "alert-danger", $("#AlertModal"));
		}
  });

  $(this).val('');  

})

$("#prjResource").change(function () {
    var selectedText = $(this).find("option:selected").val();
    var dbSchema = datas.RESOURCES[selectedText].jndiName.split('.')[1];
    console.log(selectedText);
    console.log(dbSchema);
    $('#prjDbSchema').val(dbSchema);
    if(selectedText == "XML"){
      $("#relationCount").prop("disabled", true);
      $("#relationCount").prop("checked", false);
    }
    else{
      $("#relationCount").prop("disabled", false);
      $("#relationCount").prop("checked", true);
    }
});

function initPrjLanguage(){

	$.each(countryCodes, function(i, code){
		var dc = '<span class="lang-lg lang-lbl-full" lang="' + code + '"></span> - (' + code + ')' ;
		var option = '<option class="fontsize" value="' + code + '" data-content=\'' + dc + '\'></option>';
		$("#prjLanguage").append(option);
	});
	$("#prjLanguage").selectpicker("val", emptyOption);
	$("#prjLanguage").selectpicker("refresh");
}

function GetUserInfos() {
  $.ajax({
    type: 'POST',
    url: "GetUserInfos",
    dataType: 'json',
    success: function(data) {
      // var user = 'Welcome <span class="label label-primary">' + data.USER + '</span>, choose something to do...';
      var user = 'Welcome <span class="label label-default">' + data.USER + '</span>, choose something to do...';
      $('#welcome').append(user);
      if(!data.PROJECTS){
        $("a#open").addClass('disabled');
      }
      console.log(data);
      datas = data;
    },
    error: function(data) {
      console.log(data);
    }
  });
}

function GetResources() {
  $.ajax({
    type: 'POST',
    url: "GetResources",
    dataType: 'json',
    success: function(data) {
      console.log(data);
      // var resource = {};
      // resource = data;
      datas.RESOURCES = data;
      // console.log("datas" + JSON.stringify(datas));
      console.log(datas);
      loadResources($("#prjResource"), datas.RESOURCES);
    },
    error: function(data) {
      console.log(data);
    }
  });
}

function GetLocales(){
  $.ajax({
    type: 'POST',
    url: "GetCognosLocales",
    dataType: 'json',
    success: function(data) {
      console.log(data);
      // var resource = {};
      // resource = data;
      datas.LOCALES = data.cognosLocales;
      // console.log("datas" + JSON.stringify(datas));
      console.log(datas);
      loadLocales($("#prjLanguage"), datas.LOCALES);
    },
    error: function(data) {
      console.log(data);
    }
  });

}

function NewProject() {

  var prj = {};
  prj.name = $("#prjName").val();
  prj.dbSchema = $("#prjDbSchema").val();
  prj.description = $("#prjDescription").val();
  prj.resource = datas.RESOURCES[$("#prjResource").val()];
  prj.languages = [];
  prj.languages.push($("#prjLanguage").find("option:selected").val());
  prj.relationCount = $('#relationCount').prop('checked');

  $.ajax({
    type: 'POST',
    url: "NewProject",
    dataType: 'json',
    data: JSON.stringify(prj),
    success: function(data) {
      console.log(data);
      if(data.STATUS == "KO"){
        ShowAlert(data.REASON, 'alert-warning');
      }
      if(data.STATUS == "OK"){
        window.location.replace("index.html");
      }
    },
    error: function(data) {
      console.log(data);
    }
  });
}

function OpenProject(id, ulPrj) {

  var prj = {};

  if(id == null){
    prj = ulPrj
  }
  else{
    prj = datas.PROJECTS[id];
  }

  console.log(prj);

  $.ajax({
    type: 'POST',
    url: "OpenProject",
    dataType: 'json',
    data: JSON.stringify(prj),
    success: function(data) {
      console.log(data);
      if(data.STATUS == "KO"){
        ShowAlert(data.REASON, 'alert-danger');
      }
      if(data.STATUS == "OK"){
        window.location.replace("index.html");
      }
    },
    error: function(data) {
      console.log(data);
    }
  });
}

function loadLocales(obj, list){
  obj.empty();
  $.each(list, function(i, item){
    var option = '<option class="fontsize" value="' + item + '" data-subtext="">' + item + '</option>';
    obj.append(option);
  });
  obj.selectpicker('refresh');

}


function loadResources(obj, list){
  obj.empty();
  $.each(list, function(i, item){
    var option = '';
    if(item.jndiName == "XML"){
      option = '<option class="fontsize" value="' + i + '" data-subtext="' + item.dbEngine + ' - ' + item.description + '">' + item.jndiName + '</option>';
    }
    else{
      var tableType = "";
      if(item.tableTypes != "TABLE") {tableType = '(' + item.tableTypes + ')'};

      option = '<option class="fontsize" value="' + i + '" data-subtext="' + item.dbName + ' ' + tableType +' - ' + item.dbEngine
        + ' - ' + item.cognosCatalog + ' - ' + item.cognosDataSource + ' - ' + item.cognosSchema
        + '">' + item.jndiName + '</option>';
    }
    obj.append(option);
  });
  obj.selectpicker('refresh');

}

function ShowAlert(message, alertType, $el) {

    $('#alertmsg').remove();

    var timeout = 3000;

    if(alertType.match('alert-warning')){
      timeout = 5000;
    }
    if(alertType.match('alert-danger')){
      timeout = 1000;
    }

    var $newDiv;

    if(alertType.match('alert-success|alert-info')){
      $newDiv = $('<div/>')
       .attr( 'id', 'alertmsg' )
       .html(
          '<p>' +
          message +
          '</p>'
        )
       .addClass('alert ' + alertType);
    }
    else{
      $newDiv = $('<div/>')
       .attr( 'id', 'alertmsg' )
       .html(
          '<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>' +
          '<p>' +
          '<strong>' + message + '</strong>' +
          '</p>'
        )
       .addClass('alert ' + alertType + ' alert-dismissible');
    }

    if($el){
      $el.append($newDiv);
    }
    else{
      $('#Alert').append($newDiv);
    }

    setTimeout(function() {
       $('#alertmsg').remove();
    }, timeout);

}
