var tables = [];
var modelList = [];
var $tableList = $('#tables');
var $datasTable = $('#DatasTable');
var $navTab = $('#navTab');
var $refTab = $("a[href='#Reference']");
var $finTab = $("a[href='#Final']");
var $qsTab = $("a[href='#QuerySubject']");
var $secTab = $("a[href='#Security']");
var $traTab = $("a[href='#Translation']");
var $viewTab = $("a[href='#View']");
var activeTab = "Final";
var previousTab;
var $activeSubDatasTable;
var $activeDatasTable;
var $newRowModal = $('#newRowModal');
var $modelListModal = $('#modModelList');
var $projectFileModal = $('#modProjectFile');
// var url = "js/PROJECT.json";
var qs2rm = {qs: "", row: "", qsList: [], ids2rm: {}};
var newRelation;
var cognosLocales;
var dbDataType = [];
var qsType = {isFinal: false, isRefChecked: false, isRef: false};
var Gdimensions;
var dimensionGlobal = [];
var folderGlobal = [];
var langGlobal = [];
var labelsGlobal = {};
var $selectedDimension;
var selectedField;
var selectedQS;
var currentProject;
var pKeys = {};
var fieldsView = {};

// var currentLanguage;

var countryCodes = ["ar", "be", "bg", "cs", "da", "de", "el", "en", "es", "et", "fi", "fr", "ga", "hi", "hr", "hu", "in", "is", "it", "iw", "ja", "ko", "lt", "lv", "mk", "ms", "mt", "nl", "no", "pl", "pt", "ro", "ru", "sk", "sl", "sq", "sr", "sv", "th", "tr", "uk", "vi", "zh"];
var emptyOption = '<option class="fontsize" value="" data-subtext="" data-content=""></option>';

var relationCols = [];
// relationCols.push({field:"checkbox", checkbox: "true"});
relationCols.push({field:"index", title: '<h4><span class="label label-default">Index<span></h4>', formatter: "indexFormatter", sortable: false});
// relationCols.push({field:"_id", title: "_id", sortable: true});
// relationCols.push({field:"key_name", title: "key_name", sortable: true});
relationCols.push({field:"key_type", title: '<h4><span class="label label-default">Key Type<span></h4>', sortable: true});
relationCols.push({field:"pktable_name", title: '<h4><span class="label label-default">PKTable Name<span></h4>', sortable: true});
relationCols.push({field:"pktable_alias", title: '<h4><span class="label label-default">PKTable Alias<span></h4>', class: "pktable_alias", editable: {type: "text", mode: "inline"}, sortable: true, events: "pktable_aliasEvents"});
relationCols.push({field:"label", title: '<h4><span class="label label-default">Label<span></h4>', sortable: true});
relationCols.push({field:"description", title: '<h4><span class="label label-default">Description<span></h4>', sortable: false});
relationCols.push({field:"recCountPercent", title: '<h4><span class="label label-default">Count(*) %<span></h4>', sortable: true});
relationCols.push({field:"relationship", title: '<h4><span class="label label-default">Relationship<span></h4>', editable: {type: "textarea", mode: "inline", rows: 4}});
relationCols.push({field:"fin", title: '<h4><span class="label label-default">Fin<span></h4>', formatter: "boolFormatter", align: "center"});
relationCols.push({field:"ref", title: '<h4><span class="label label-default">Ref<span></h4>', formatter: "boolFormatter", align: "center"});
relationCols.push({field:"sec", title: '<h4><span class="label label-default">Sec<span></h4>', formatter: "boolFormatter", align: "center"});
relationCols.push({field:"tra", title: '<h4><span class="label label-default">Tra<span></h4>', formatter: "boolFormatter", align: "center"});
relationCols.push({field:"nommageRep", title: '<h4><span class="label label-default">RepTableName<span></h4>', formatter: "boolFormatter", align: "center"});
relationCols.push({field:"above", title: '<h4><span class="label label-default">Above<span></h4>', editable: {type: "text", mode: "inline"}, align: "center"});
// relationCols.push({field:"above", title: "Above", formatter: "aboveFormatter", align: "center", events: "aboveEvents"});
// relationCols.push({field:"above", title: "Above", formatter: "aboveFormatter", align: "center", events: "aboveEvents"});
relationCols.push({field:"leftJoin", title: '<h4><span class="label label-default">Left Join<span></h4>', formatter: "boolFormatter", align: "center"});
relationCols.push({field:"rightJoin", title: '<h4><span class="label label-default">Right Join<span></h4>', formatter: "boolFormatter", align: "center"});
// relationCols.push({field:"usedForDimensions", title: "Used For Dimensions", formatter: "boolFormatter", align: "center"});
relationCols.push({field:"usedForDimensions", title: '<h4><span class="label label-default">Used For Dimensions<span></h4>', editable: {
  type: "select", mode: "inline", value: "",
  source: function(){
    var source = [];
    // source.push({"text": "", "value": ""});

    $("#dimSelect option").each(function(){
      var option = {};
      option.text = $(this).val();
      option.value = $(this).val();
      source.push(option);
    });
    return source;
  },
}, align: "center"});

var usedForDimensionsSelect = {
  type: "select",
  mode: "inline"
};


relationCols.push({field:"duplicate", title: '<i class="glyphicon glyphicon-duplicate"></i>', formatter: "duplicateFormatter", align: "center"});
relationCols.push({field:"remove", title: '<i class="glyphicon glyphicon-trash"></i>', formatter: "removeRelationFormatter", align: "center"});
// relationCols.push({field:"operate", title: "operate", formatter: "operateRelationFormatter", align: "center", events: "operateRelationEvents"});

// relationCols.push({field:"linker", formatter: "boolFormatter", align: "center", title: "linker"});
// relationCols.push({field:"linker_ids", title: "linker_ids"});

window.pktable_aliasEvents = {
      'change .pktable_alias': function (e, value, row, index) {
        alert("value=" + value);
      }
}

// function pktable_aliasFormatter(value, row, index){
//   return '<a href="#" id="pktable_alias">' + value + 'superuser</a>'
// }
//
$('pktable_alias .editable').on('update', function(e, editable) {
    alert('new value: ' + editable.value);
});

var newRelationCols = [];

newRelationCols.push();

var qsCols = [];
// qsCols.push({field:"checkbox", checkbox: "true"});
qsCols.push({field:"index", title: '<h4><span class="label label-default">Index<span>', formatter: "indexFormatter", sortable: false});
// qsCols.push({field:"_id", title: "_id", sortable: true});
qsCols.push({field:"table_name", title: '<h4><span class="label label-default">Table Name<span>', sortable: true});
qsCols.push({field:"table_alias", title: '<h4><span class="label label-default">Table Alias<span>', editable: false, sortable: true});
qsCols.push({field:"type", title: '<h4><span class="label label-default">Type<span>', sortable: true});
// qsCols.push({field:"visible", title: "visible", formatter: "boolFormatter", align: "center", sortable: false});

qsCols.push({field:"folder", title: '<h4><span class="label label-default">Folder<span>', editable: {
  type: "select",
  mode: "inline",
  value: "",
  source: function(){
    var source = [];
    // source.push({"text": "", "value": ""});

    $("#foldSelect option").each(function(){
      var option = {};
      option.text = $(this).val();
      option.value = $(this).val();
      source.push(option);
    });
    return source;

  },
  sortable: true}
});
// qsCols.push({field:"folder", title: "Folder", editable: {type: "select", mode: "inline"}, sortable: true});
// qsCols.push({field:"folder", title: "Folder", editable: {type: "text", mode: "inline"}, sortable: true});
qsCols.push({field:"filters", title: '<h4><span class="label label-default">Filters<span>', formatter: "filtersFormatter", align: "center"});
qsCols.push({field:"secFilter", title: '<h4><span class="label label-default">Security Filters<span>', editable: {type: "textarea", mode: "inline"}, sortable: true});
qsCols.push({field:"label", title: '<h4><span class="label label-default">Label<span>', editable: {type: "textarea", mode: "inline"}, sortable: true});
qsCols.push({field:"description", title: '<h4><span class="label label-default">Description<span>', sortable: false, editable: {type: "textarea", mode: "inline", rows: 4}});
qsCols.push({field:"merge", title: '<h4><span class="label label-default">Merge<span>', sortable: false, editable: {type: "textarea", mode: "inline", rows: 2}});
qsCols.push({field:"recCount", title: '<h4><span class="label label-default">Count(*)<span>', sortable: true});
qsCols.push({field:"root", title: '<h4><span class="label label-default">Root<span>', formatter: "boolFormatter", align: "center"});
qsCols.push({field:"recurseCount", title: '<i class="glyphicon glyphicon-repeat" title="Set recurse count"></i>', editable: {
    type: "select",
    mode: "inline",
    value: 1,
  //   source: [
  //     {value: 1, text: 1},
  //     {value: 2, text: 2},
  //     {value: 3, text: 3},
  //     {value: 4, text: 4},
  //     {value: 5, text: 5}
  //     ],
    source: function(){
      var result = [];
      for(var i = 1; i < 21; i++){
        var option = {};
        option.value = i;
        option.text = i;
        result.push(option);
      }
      return result;
    },
    align: "center"}
  });
qsCols.push({field:"addPKRelation", title: '<i class="glyphicon glyphicon-magnet" title="Add PK relation(s)"></i>', formatter: "addPKRelationFormatter", align: "center"});
qsCols.push({field:"addRelation", title: '<i class="glyphicon glyphicon-plus-sign" title="Add new relation"></i>', formatter: "addRelationFormatter", align: "center"});
qsCols.push({field:"addField", title: '<i class="glyphicon glyphicon-plus-sign" title="Add new field"></i>', formatter: "addFieldFormatter", align: "center"});
qsCols.push({field:"addFields", title: '<i class="glyphicon glyphicon-plus-sign" title="Add fields"></i>', formatter: "addFieldsFormatter", align: "center"});
// qsCols.push({field:"addFolder", title: '<i class="glyphicon glyphicon-folder-open" title="Add new folder name"></i>', formatter: "addFolderFormatter", align: "center"});
// qsCols.push({field:"addDimensionName", title: '<i class="glyphicon glyphicon-zoom-in" title="Add new dimension name"></i>', formatter: "addDimensionNameFormatter", align: "center"});
qsCols.push({field:"remove", title: '<i class="glyphicon glyphicon-trash"></i>', formatter: "removeRootTableFormatter", align: "center"});
qsCols.push({field:"linker", formatter: "boolFormatter", title: "linker", align: "center"});
qsCols.push({field:"linker_ids", title: "linker_ids"});

var fieldCols = [];
// fieldCols.push({field:"checkbox", checkbox: "true"});
fieldCols.push({field:"index", title: '<h4><span class="label label-default">index</span>', formatter: "indexFormatter", sortable: false});
fieldCols.push({field:"_id", title: '<h4><span class="label label-default">Id</span>', sortable: true});
fieldCols.push({field:"fieldPos", title: '<h4><span class="label label-default">Pos</span>', sortable: true});
fieldCols.push({field:"field_name", title: '<h4><span class="label label-default">Name</span>', sortable: true });
fieldCols.push({field:"alias", title: '<h4><span class="label label-default">Alias</span>', sortable: false, editable: {type: "textarea", mode: "inline", rows: 2}});
fieldCols.push({class:"field_type", field:'field_type', title: '<h4><span class="label label-default">Type</span>', editable: {type: "text", mode: "inline"}, sortable: true});

var customFieldType = {
  type: "select",
  mode: "inline"
};

fieldCols.push({field:"label", title: '<h4><span class="label label-default">Label</span>', editable: {type: "text", mode: "inline"}, sortable: true});
fieldCols.push({field:"description", title: '<h4><span class="label label-default">Description</span>', sortable: false, editable: {type: "textarea", mode: "inline", rows: 4}});
fieldCols.push({field:"expression", title: '<h4><span class="label label-default">Expression</span>', sortable: false, formatter: "expressionFormatter", align: "center"});
fieldCols.push({field:"traduction", title: '<h4><span class="label label-default">Traduction</span>', formatter: "boolFormatter", align: "center", sortable: false});
fieldCols.push({field:"hidden", title: '<h4><span class="label label-default">Hidden</span>', formatter: "hiddenFormatter", align: "center", sortable: false});
// fieldCols.push({field:"field_type", title: '<h4><span class="label label-default">field_type</span>', editable: false, sortable: true});
// fieldCols.push({field:"field_size", title: '<h4><span class="label label-default">field_size</span>', editable: false, sortable: true});
// fieldCols.push({field:"nullable", title: '<h4><span class="label label-default">nullable</span>', editable: false, sortable: true});
// fieldCols.push({field:"timezone", title: '<h4><span class="label label-default">timezone</span>', formatter: "boolFormatter", align: "center", sortable: false});
fieldCols.push({field:"icon", title: '<h4><span class="label label-default">Icon</span>', editable:{
  type: "select",
  mode: "inline",
  value: "Attribute",
  source: [{value: "Attribute", text: "Attribute"}, {value: "Identifier", text: "Identifier"}, {value: "Fact", text: "Fact"}]
  }
});
fieldCols.push({field:"displayType", title: '<h4><span class="label label-default">Display Type</span>', editable:{
  type: "select",
  mode: "inline",
  value: "Value",
  source: [{value: "Link", text: "Link"}, {value: "Picture", text: "Picture"}, {value: "Value", text: "Value"}]
  }
});

var measure = {
  type: "select",
  mode: "inline",
  value: [],
  source: [
      {value: '', text: ''},
      {value: 'Average', text: 'Average'},
      {value: 'Count', text: 'Count'},
      {value: 'Count Distinct', text: 'Count Distinct'},
      {value: 'Sum', text: 'Sum'},
      {value: 'Maximun', text: 'Maximun'},
      {value: 'Minimum', text: 'Minimum'},
      {value: 'Median', text: 'Median'},
      {value: 'Standard Deviation', text: 'Standard Deviation'},
      {value: 'Variance', text: 'Variance'}
  ]
};

// fieldCols.push({field:"merge", title: '<h4><span class="label label-default">Merge<span>', sortable: false, editable: {type: "textarea", mode: "inline", rows: 2}});

fieldCols.push({field:"measure", title: '<h4><span class="label label-default">Measure</span>', editable: measure});
fieldCols.push({field:"dimensions", title: '<h4><span class="label label-default">Dimensions</span>', formatter: "dimensionsFormatter", align: "center"});

var dateDimensions = {
  type: "checklist",
  mode: "inline",
  value: [],
  source: [
    {value: 'Year', text: 'Year'},
    {value: 'Quarter', text: 'Quarter'},
    {value: 'Month', text: 'Month'},
    {value: 'Weeks', text: 'Weeks'},
    {value: 'Day', text: 'Day'},
    {value: 'AM/PM', text: 'AM/PM'},
    {value: 'Hour', text: 'Hour'},
    {value: 'Min', text: 'Min'},
    {value: 'Date', text: 'Date'}
  ]
}

// fieldCols.push({field:"dimension", title: "Dimension", editable: {type: 'text', mode: 'inline'}});
// fieldCols.push({field:"order", title: "Order", editable: {type: "textarea", mode: "inline", rows: 2}, sortable: true});
// fieldCols.push({field:"bk", title: "BK", editable: {type: "textarea", mode: "inline", rows: 2}, sortable: true});
// fieldCols.push({field:"hierarchyName", title: "Hierarchy Name", editable: {type: "text", mode: "inline"}, sortable: true});
// fieldCols.push({field:"buildDrillPath", title: '<i class="glyphicon glyphicon-zoom-in"></i>', formatter: "buildDrillPathFormatter", align: "center"});
fieldCols.push({field:"addDimension", title: '<i class="glyphicon glyphicon-plus-sign" title="Add new dimension"></i>', formatter: "addDimensionFormatter", align: "center"});

fieldCols.push({field:"folder", title: '<h4><span class="label label-default">Folder</span>', sortable: false, editable: {type: "textarea", mode: "inline", rows: 2}});
fieldCols.push({field:"role", title: '<h4><span class="label label-default">Role</span>', sortable: true });

fieldCols.push({field:"remove", title: '<i class="glyphicon glyphicon-trash"></i>', formatter: "removeFieldFormatter", align: "center"});




var dimensionCols = [];
dimensionCols.push({field:"index", title: '<h4><span class="label label-default">index</span></h4>', formatter: "indexFormatter", sortable: false});
dimensionCols.push({field:"dimension", title: '<h4><span class="label label-default">Dimension</span></h4>', editable: {type: 'text', mode: 'inline'}});
dimensionCols.push({field:"order", title: '<h4><span class="label label-default">Order</span></h4>', editable: {type: "textarea", mode: "inline", rows: 2}, sortable: true});
dimensionCols.push({field:"bk", title: '<h4><span class="label label-default">BK</span></h4>', editable: {type: "textarea", mode: "inline", rows: 4}, sortable: true});
dimensionCols.push({field:"attributs", title: '<h4><span class="label label-default">Attributs</span></h4>', editable: {type: "textarea", mode: "inline", rows: 4}, sortable: true});
dimensionCols.push({field:"hierarchyName", title: '<h4><span class="label label-default">Hierarchy Name</span></h4>', editable: {type: "text", mode: "inline"}, sortable: true});
dimensionCols.push({field:"buildDrillPath", title: '<i class="glyphicon glyphicon-zoom-in"></i>', formatter: "buildDrillPathFormatter", align: "center"});
dimensionCols.push({field:"remove", title: '<i class="glyphicon glyphicon-trash"></i>', formatter: "removeDimensionFormatter", align: "center"});

$(document)
.ready(function() {
  // localStorage.setItem('dbmd', null);

  buildTable($datasTable, qsCols, [], true);
  buildTable($('#ViewsTable'), qsCols, [], true);
  $('#ViewsTable').hide();
  // buildComboList($('#selectDimension'));
  // GetCognosLocales();
  initLangList();
  GetCurrentProject();
  $viewTab.prop('disabled',true);

})
.on('hidden.bs.modal', '.modal', function () {
    $('.modal:visible').length && $(document.body).addClass('modal-open');
})
.ajaxStart(function(){
    $("div#divLoading").addClass('show');
		$("div#modDivLoading").addClass('show');
})
.ajaxStop(function(){
    $("div#divLoading").removeClass('show');
		$("div#modDivLoading").removeClass('show');
});

$tableList.change(function () {
    var selectedText = $(this).find("option:selected").val();
		$('#alias').val(selectedText);
});

$tableList.on('show.bs.select', function (e) {
  // do something...
  // console.log("$tableList.on('show.bs.select'");
  // ChooseTable($tableList);

});

// $navTab.on('shown.bs.tab', function(event){
//     activeTab = $(event.target).text();         // active tab
// 		console.log("Event shown.bs.tab: activeTab=" + activeTab);
//     previousTab = $(event.relatedTarget).text();  // previous tab
// 		console.log("Event shown.bs.tab: previousTab=" + previousTab);
// });

$('#pktable_alias').on('save', function(e, params) {
    alert('Saved value: ' + params.newValue);
});

$navTab.on('show.bs.tab', function(event){
    activeTab = $(event.target).text();         // active tab
    activeTabObject = $(event.target);
		console.log("Event show.bs.tab: activeTab=" + activeTab);
    console.log(activeTabObject);
    previousTab = $(event.relatedTarget).text();  // previous tab
    previousTabObject = $(event.relatedTarget);
		console.log("Event show.bs.tab: previousTab=" + previousTab);
    console.log(previousTabObject);
});

$qsTab.on('shown.bs.tab', function(e) {
  var datas = $datasTable.bootstrapTable('getData');
  buildTable($datasTable, qsCols, datas, true, fieldCols, "fields");
  $datasTable.bootstrapTable("filterBy", {});
  // $datasTable.bootstrapTable('showColumn', 'checkbox');
  $datasTable.bootstrapTable('showColumn', 'visible');
  $datasTable.bootstrapTable('showColumn', 'filter');
  $datasTable.bootstrapTable('showColumn', 'secFilter');
  $datasTable.bootstrapTable('showColumn', 'label');
  $datasTable.bootstrapTable('hideColumn', 'operate');
  $datasTable.bootstrapTable('hideColumn', 'addRelation');
  $datasTable.bootstrapTable('hideColumn', 'addPKRelation');
  $datasTable.bootstrapTable('showColumn', 'addField');
  $datasTable.bootstrapTable('hideColumn', 'addFields');
  $datasTable.bootstrapTable('showColumn', 'merge');
  // $datasTable.bootstrapTable('showColumn', 'addFolder');
  // $datasTable.bootstrapTable('showColumn', 'addDimensionName');
  $datasTable.bootstrapTable('hideColumn', 'recurseCount');
  $datasTable.bootstrapTable('hideColumn', '_id');
  $datasTable.bootstrapTable('hideColumn', 'above');
  $datasTable.bootstrapTable('showColumn', 'folder');
  $datasTable.bootstrapTable('hideColumn', 'linker');
  $datasTable.bootstrapTable('hideColumn', 'linker_ids');
  $datasTable.bootstrapTable('hideColumn', 'remove');
  $datasTable.bootstrapTable('hideColumn', 'root');

  // $("#foldInputGroup").hide().addClass('hidden');
  // $("#foldInputGroup").hide().addClass('show');
    // $("#dimInputGroup").addClass('show');
  // $("#dimInputGroup").hide().addClass('show');
    // $("#hierInputGroup").addClass('show');
  // $("#hierInputGroup").hide().addClass('show');

  $datasTable.show();
  $('#ViewsTable').hide();
});

// $viewTab.on('hide.bs.tab', function(e) {
//   views = $('#DatasTable').bootstrapTable('getData');
// 	$('#DatasTable').bootstrapTable('load', datas);
// })

$viewTab.on('shown.bs.tab', function(e) {
  var views = $('#ViewsTable').bootstrapTable('getData');
  console.log(views);
  // datas = $('#DatasTable').bootstrapTable('getData');
	// $('#ViewsTable').bootstrapTable('load', views);
  buildTable($('#ViewsTable'), qsCols, views, true, fieldCols, "fields");
  $('#ViewsTable').bootstrapTable("filterBy", {});
  // $('#ViewsTable').bootstrapTable('showColumn', 'checkbox');
  $('#ViewsTable').bootstrapTable('hideColumn', 'visible');
  $('#ViewsTable').bootstrapTable('hideColumn', 'filter');
  $('#ViewsTable').bootstrapTable('hideColumn', 'secFilter');
  $('#ViewsTable').bootstrapTable('showColumn', 'label');
  $('#ViewsTable').bootstrapTable('hideColumn', 'operate');
  $('#ViewsTable').bootstrapTable('hideColumn', 'addRelation');
  $('#ViewsTable').bootstrapTable('hideColumn', 'addPKRelation');
  $('#ViewsTable').bootstrapTable('showColumn', 'addField');
  $('#ViewsTable').bootstrapTable('showColumn', 'addFields');
  $('#ViewsTable').bootstrapTable('hideColumn', 'merge');
  // $('#ViewsTable').bootstrapTable('hideColumn', 'table_alias');
  // $('#ViewsTable').bootstrapTable('showColumn', 'addFolder');
  // $('#ViewsTable').bootstrapTable('showColumn', 'addDimensionName');
  $('#ViewsTable').bootstrapTable('hideColumn', 'recurseCount');
  $('#ViewsTable').bootstrapTable('hideColumn', '_id');
  $('#ViewsTable').bootstrapTable('hideColumn', 'above');
  $('#ViewsTable').bootstrapTable('hideColumn', 'folder');
  $('#ViewsTable').bootstrapTable('hideColumn', 'linker');
  $('#ViewsTable').bootstrapTable('hideColumn', 'linker_ids');
  $('#ViewsTable').bootstrapTable('hideColumn', 'remove');
  $('#ViewsTable').bootstrapTable('hideColumn', 'recCount');
  $('#ViewsTable').bootstrapTable('hideColumn', 'root');

  // $("#foldInputGroup").hide().addClass('hidden');
  // $("#foldInputGroup").hide().addClass('show');
    // $("#dimInputGroup").addClass('show');
  // $("#dimInputGroup").hide().addClass('show');
    // $("#hierInputGroup").addClass('show');
  // $("#hierInputGroup").hide().addClass('show');
  $datasTable.hide();
  $('#ViewsTable').show();

});

$finTab.on('shown.bs.tab', function(e) {
  var datas = $datasTable.bootstrapTable('getData');
  buildTable($datasTable, qsCols, datas, true, relationCols, "relations");
  $datasTable.bootstrapTable("filterBy", {});
  $datasTable.bootstrapTable("filterBy", {type: ['Final']});
  // $datasTable.bootstrapTable('hideColumn', 'checkbox');
  $datasTable.bootstrapTable('hideColumn', 'key_name');
  $datasTable.bootstrapTable('showColumn', 'operate');
  $datasTable.bootstrapTable('hideColumn', 'visible');
  $datasTable.bootstrapTable('hideColumn', 'filter');
  $datasTable.bootstrapTable('hideColumn', 'secFilter');
  $datasTable.bootstrapTable('showColumn', 'label');
  $datasTable.bootstrapTable('hideColumn', 'recurseCount');
  $datasTable.bootstrapTable('showColumn', 'addRelation');
  $datasTable.bootstrapTable('hideColumn', 'addDimensionName');
  $datasTable.bootstrapTable('hideColumn', 'addField');
  $datasTable.bootstrapTable('hideColumn', 'addFields');
  $datasTable.bootstrapTable('hideColumn', 'merge');
  $datasTable.bootstrapTable('hideColumn', 'addPKRelation');
  // $datasTable.bootstrapTable('hideColumn', 'addFolder');
  // $datasTable.bootstrapTable('hideColumn', 'addDimension');
  $datasTable.bootstrapTable('hideColumn', 'nommageRep');
  $datasTable.bootstrapTable('hideColumn', '_id');
  $datasTable.bootstrapTable('hideColumn', 'linker');
  $datasTable.bootstrapTable('hideColumn', 'linker_ids');
  $datasTable.bootstrapTable('hideColumn', 'above');
  $datasTable.bootstrapTable('hideColumn', 'folder');
  $datasTable.bootstrapTable('showColumn', 'remove');
  $datasTable.bootstrapTable('showColumn', 'root');
  $datasTable.show();
  $('#ViewsTable').hide();

});

$refTab.on('shown.bs.tab', function(e) {
  var datas = $datasTable.bootstrapTable('getData');
  buildTable($datasTable, qsCols, datas, true, relationCols, "relations");
  $datasTable.bootstrapTable("filterBy", {});
  // $datasTable.bootstrapTable("filterBy", {type: ['Final', 'Ref']});
  // $datasTable.bootstrapTable('hideColumn', 'checkbox');
  $datasTable.bootstrapTable('hideColumn', 'key_name');
  $datasTable.bootstrapTable('showColumn', 'operate');
  $datasTable.bootstrapTable('hideColumn', 'visible');
  $datasTable.bootstrapTable('hideColumn', 'filter');
  $datasTable.bootstrapTable('hideColumn', 'secFilter');
  $datasTable.bootstrapTable('showColumn', 'label');
  $datasTable.bootstrapTable('showColumn', 'addPKRelation');
  // $datasTable.bootstrapTable('hideColumn', 'addFolder');
  // $datasTable.bootstrapTable('hideColumn', 'addDimension');
  $datasTable.bootstrapTable('hideColumn', 'addDimensionName');
  $datasTable.bootstrapTable('showColumn', 'addRelation');
  $datasTable.bootstrapTable('showColumn', 'above');
  $datasTable.bootstrapTable('hideColumn', 'addField');
  $datasTable.bootstrapTable('hideColumn', 'addFields');
  $datasTable.bootstrapTable('hideColumn', 'merge');
  $datasTable.bootstrapTable('showColumn', 'recurseCount');
  $datasTable.bootstrapTable('showColumn', 'nommageRep');
  $datasTable.bootstrapTable('hideColumn', '_id');
  $datasTable.bootstrapTable('hideColumn', 'linker');
  $datasTable.bootstrapTable('hideColumn', 'linker_ids');
  $datasTable.bootstrapTable('hideColumn', 'folder');
  $datasTable.bootstrapTable('hideColumn', 'remove');
  $datasTable.bootstrapTable('hideColumn', 'root');
  $datasTable.show();
  $('#ViewsTable').hide();

});

$secTab.on('shown.bs.tab', function(e) {
  var datas = $datasTable.bootstrapTable('getData');
  buildTable($datasTable, qsCols, datas, true, relationCols, "relations");
  $datasTable.bootstrapTable("filterBy", {});
  $datasTable.bootstrapTable("filterBy", {type: ['Final', 'Ref', 'Sec']});
  // $datasTable.bootstrapTable('hideColumn', 'checkbox');
  $datasTable.bootstrapTable('hideColumn', 'key_name');
  $datasTable.bootstrapTable('showColumn', 'operate');
  $datasTable.bootstrapTable('hideColumn', 'visible');
  $datasTable.bootstrapTable('hideColumn', 'filter');
  $datasTable.bootstrapTable('hideColumn', 'secFilter');
  $datasTable.bootstrapTable('showColumn', 'label');
  $datasTable.bootstrapTable('showColumn', 'addPKRelation');
  // $datasTable.bootstrapTable('hideColumn', 'addFolder');
  // $datasTable.bootstrapTable('hideColumn', 'addDimension');
  $datasTable.bootstrapTable('hideColumn', 'addDimensionName');
  $datasTable.bootstrapTable('showColumn', 'addRelation');
  $datasTable.bootstrapTable('hideColumn', 'addField');
  $datasTable.bootstrapTable('hideColumn', 'addFields');
  $datasTable.bootstrapTable('hideColumn', 'merge');
  $datasTable.bootstrapTable('showColumn', 'recurseCount');
  $datasTable.bootstrapTable('showColumn', 'nommageRep');
  $datasTable.bootstrapTable('showColumn', 'above');
  $datasTable.bootstrapTable('hideColumn', 'folder');
  $datasTable.bootstrapTable('hideColumn', 'linker');
  $datasTable.bootstrapTable('hideColumn', 'linker_ids');
  $datasTable.bootstrapTable('hideColumn', 'root');
  $datasTable.show();
  $('#ViewsTable').hide();


});

$traTab.on('shown.bs.tab', function(e) {
  var datas = $datasTable.bootstrapTable('getData');
  buildTable($datasTable, qsCols, datas, true, relationCols, "relations");
  $datasTable.bootstrapTable("filterBy", {});
  $datasTable.bootstrapTable("filterBy", {type: ['Final', 'Ref', 'Tra']});
  $datasTable.bootstrapTable('showColumn', 'operate');
  $datasTable.bootstrapTable('hideColumn', 'visible');
  $datasTable.bootstrapTable('hideColumn', 'filter');
  $datasTable.bootstrapTable('hideColumn', 'secFilter');
  $datasTable.bootstrapTable('showColumn', 'label');
  $datasTable.bootstrapTable('showColumn', 'addPKRelation');
  $datasTable.bootstrapTable('showColumn', 'addRelation');
  $datasTable.bootstrapTable('showColumn', 'recurseCount');
  $datasTable.bootstrapTable('showColumn', 'nommageRep');
  $datasTable.bootstrapTable('showColumn', 'above');
  $datasTable.bootstrapTable('hideColumn', 'merge');
  $datasTable.bootstrapTable('hideColumn', 'root');
  $datasTable.show();
  $('#ViewsTable').hide();

});


// $datasTable.on('editable-save.bs.table', function (editable, field, row, oldValue, $el) {
//   console.log("row");
//   console.log(row);
//   console.log("$el");
//   console.log($el);
//   row._id = row.key_type + 'K_' + row.pktable_alias + '_' + row.table_alias + '_' + row.type;
//   if(field == "pktable_alias"){
//     var newValue = row.pktable_alias;
//     if($activeSubDatasTable != undefined){
//       updateCell($activeSubDatasTable, row.index, 'relationship', row.relationship.split("[" + oldValue + "]").join("[" + newValue + "]"));
//     }
//   }
// });

$datasTable.on('resetrr-view.bs.table', function(){
  // console.log("++++++++++++++on passe dans reset-view");
  // console.log("activeTab=" + activeTab);
  // console.log("previousTab=" + previousTab);
  if($activeSubDatasTable != undefined){
    var v = $activeSubDatasTable.bootstrapTable('getData');
    // console.log("+++++++++++ $activeSubDatasTable");
    // console.log(v);
    var $tableRows = $activeSubDatasTable.find('tbody tr');
    // console.log("++++++++++ $tableRows");
    // console.log($tableRows);
    $.each(v, function(i, row){
      // console.log("row.ref");
      // console.log(row.ref);
      // console.log("row.fin");
      // console.log(row.fin);

      // Disable RepTableName if !ref
      // if(activeTab == "Reference" && !row.ref){
      //   $tableRows.eq(i).find('a').eq(4).editable('disable');
      // }
      if(activeTab == "Security" && !row.sec){
        $tableRows.eq(i).find('a').eq(4).editable('disable');
        // $tableRows.eq(i).find('a').editable('disable');
      }
      if(row.fin || row.ref || row.sec){
        $tableRows.eq(i).find('a').eq(0).editable('disable');
        // $tableRows.eq(i).find('a').editable('disable');
      }
      if(row.fin && activeTab.match("Reference|Security")){
        $tableRows.eq(i).find('a').eq(3).editable('disable');
        $tableRows.eq(i).find('a').eq(2).editable('disable');
      }
      if(row.ref && activeTab.match("Final|Security")){
        $tableRows.eq(i).find('a').eq(3).editable('disable');
        $tableRows.eq(i).find('a').eq(2).editable('disable');
      }
      if(row.sec && activeTab.match("Final|Reference")){
        $tableRows.eq(i).find('a').eq(3).editable('disable');
        $tableRows.eq(i).find('a').eq(2).editable('disable');
      }
      // if(activeTab.match("Query Subject") && row.field_type != undefined){
      //   if(row.field_type.toUpperCase() == "DATE" || row.field_type.toUpperCase() == "TIMESTAMP" || row.field_type.toUpperCase() == "DATETIME"){
      //     $tableRows.eq(i).find('a').eq(8).editable('destroy');
      //     $tableRows.eq(i).find('a').eq(8).editable(dateDimensions);
      //   // $tableRows.eq(i).find('a').eq(6).editable('option', 'source', dateDimensions.source);
      //   }
      //   else{
      //     // $tableRows.eq(i).find('a').eq(6).editable('option', 'source', dimensions.source);
      //   }
      // }
      if(activeTab.match("Query Subject")){
        if(row.custom != true){
          $tableRows.eq(i).find('a').eq(0).editable('disable');
          // console.log($tableRows.eq(i).find('a.remove').val('hidden'));
          // console.log(row);
          // $tableRows.eq(i).find('a.remove').prop("disabled",true);
          $tableRows.eq(i).find('a.remove').remove();
        }
        else{
          $tableRows.eq(i).find('a').eq(0).editable('destroy');
          // $tableRows.eq(i).find('a').eq(0).editable('setValue', ['VARCHAR']);
          customFieldType.source = dbDataType;
          $tableRows.eq(i).find('a').eq(0).editable(customFieldType);
          $tableRows.eq(i).find('a').eq(0).editable('option', 'defaultValue', '');
        }
      }

    });
  }
});

$datasTable.on('expand-row.bs.table', function (index, row, $detail) {
  // console.log("index: ");
  // console.log(index);
  // console.log("row: ");
  // console.log(row);
  // console.log("$detail: ");
  // console.log($detail);

});

$newRowModal.on('show.bs.modal', function (e) {
  // do something...
	// ChooseQuerySubject($('#modQuerySubject'));
  $('#modRelationship').val('');
  $('#modPKTable').empty();
  $('#modPKColumn').empty();
  $('#modKeyType').empty();
  $('#modPKColumn').selectpicker('refresh');

  if(activeTab == "Final"){
    $('#modKeyType').append('<option value="F">Foreign</option>');
  }
  if(activeTab.match("Reference|Security|Translation")){
    $('#modKeyType').append('<option value="F">Foreign</option>');
    $('#modKeyType').append('<option value="P">Primary</option>');
  }
  $('#modKeyType').selectpicker('refresh');

  // var tables = JSON.parse(localStorage.getItem('tables'));
  // $.each(tables, function(i, obj){
  //   var option = '<option class="fontsize" value=' + obj.name + '>' + obj.name + ' (' + obj.remarks + ') (' + obj.FKCount + ') (' + obj.FKSeqCount + ')'
  //    + ' (' + obj.PKCount + ') (' + obj.PKSeqCount + ') (' + obj.RecCount + ')' + '</option>';
  //   $('#modPKTables').append(option);
  // });
  // $('#modPKTables').selectpicker('refresh');
	ChooseTable($('#modPKTable'));
  // $(this)
  // .find('.modal-body')
  // .load("sqel.html", function(){
  //});

})

$modelListModal.on('shown.bs.modal', function() {
  $(this).find('.modal-body').empty();
  var list = '<div class="container-fluid"><div class="row"><form role="form"><div class="form-group">';
  list += '<input id="searchinput" class="form-control" type="search" placeholder="Search..." /></div>';
  list += '<div id="searchlist" class="list-group">';

  $.each(modelList, function(index, object){
    list += '<a href="#" class="list-group-item" onClick="OpenModel(' + object.id + '); return false;"><span>' + object.name + '</span></a>';
  });
  list += '</div></form><script>$("#searchlist").btsListFilter("#searchinput", {itemChild: "span", initial: false, casesensitive: false});</script>';
  $(this).find('.modal-body').append(list);
});


$projectFileModal.on('shown.bs.modal', function() {
    $(this).find('.modal-body').empty();
    var html = [
      '<div class="container-fluid"><div class="row"><div class="form-group"><div class="input-group">',
	'<span class="input-group-addon">model-</span>',
      '<input type="text" id="filePath" class="form-control">',
      '</div></div></div></div>',
    ].join('');

    $(this).find('.modal-body').append(html);
    $(this).find('#filePath').focus().val("NNN");


});

$('#modPKTable').change(function () {
    var selectedText = $(this).find("option:selected").val();
    ChooseField($('#modPKColumn'), selectedText);
});

window.operateRelationEvents = {
    'click .duplicate': function (e, value, row, index) {
      console.log("+++++ on entre dans click .duplicate");
      console.log(e);
      console.log(value);
      console.log(row);
      console.log(index);

        nextIndex = row.index + 1;
        console.log("nextIndex=" + nextIndex);
        var newRow = $.extend({}, row);
        newRow.checkbox = false;
        newRow.pktable_alias = "";
        newRow.fin = false;
        newRow.ref = false;
        newRow.relationship = newRow.relationship.replace(/ = \[FINAL\]\./g, " = ");
        newRow.relationship = newRow.relationship.replace(/ = \[REF\]\./g, " = ");
        console.log("newRow");
        console.log(newRow);
        $activeSubDatasTable.bootstrapTable('insertRow', {index: nextIndex, row: newRow});
        console.log("+++++ on sort de click .duplicate");

    },
    'click .remove': function (e, value, row, index) {
        $activeSubDatasTable.bootstrapTable('remove', {
            field: 'index',
            values: [row.index]
        });
    }
};

window.operateQSEvents = {
    'click .addRelation': function (e, value, row, index) {

      console.log("index=" + index);
      // $datasTable.bootstrapTable('expandAllRows');
      $datasTable.bootstrapTable('expandRow', index);

      console.log("++++++++++++++on passe dans window.operateQSEvents.add");
      if($activeSubDatasTable != ""){

        var v = $activeSubDatasTable.bootstrapTable('getData');
        console.log("+++++++++++ $activeSubDatasTable");

        console.log(v);
        $newRowModal.modal('toggle');
        var qs = row.table_alias + ' - ' + row.type + ' - ' + row.table_name;
        // $('#modQuerySubject').selectpicker('val', qs);

        $('#modQuerySubject').text(qs);
        $('#modKeyName').val("CK_" + row.table_alias);
        $('#modPKTableAlias').val("");
        // $('#modRelathionship').val("[" + row.type.toUpperCase() + "].[" + row.table_alias + "].[] = ");
      }

    },
    'click .expandAllQS': function (e, value, row, index) {
      $datasTable.bootstrapTable("expandAllRows")
    },
    'click .collapseAllQS': function (e, value, row, index) {
      $datasTable.bootstrapTable("collapseAllRows")
    }
};

window.aboveEvents = {
  'change .Select1': function (e, value, row, index){
    var selectedText = $("#Select1").find("option:selected").val();
    row.above = selectedText;
    console.log(e);
    console.log(value);
    console.log(row);
    console.log(index);
    console.log($activeSubDatasTable);
    // updateCell($activeSubDatasTable, index, 'above', selectedText);
    // updateRow($activeSubDatasTable, index, row);
  }

};

function BuildTestQuery(query, type, tables){

  var parms = {query: query, type: type, tables: tables};

  console.log(parms);

  $.ajax({
		type: 'POST',
		url: "BuildTestQuery",
		dataType: 'json',
    data: JSON.stringify(parms),

		success: function(query) {
      console.log(query);
      WatchContent(query.query);
		},
		error: function(data) {
			showalert("BuildTestQuery()", "Getting test query failed.", "alert-danger", "bottom");
		}
	});

}

function WatchContent(query){
  // var query = "select * from " + table;
  console.log(query);
  var parms = {query: query};
  localStorage.setItem('SQLQuery', JSON.stringify(parms));
  window.open("watchContent.html");
}


function buildComboList($el) {

  var content = "<input type='text' class='bss-input' onKeyDown='event.stopPropagation();' onKeyPress='addSelectInpKeyPress(this,event)' onClick='event.stopPropagation()' placeholder='Add item'> <span class='glyphicon glyphicon-plus addnewicon' onClick='addSelectItem(this,event,1);'></span>";
  // var content = "<input type='text' class='bss-input' onKeyDown='event.stopPropagation();' onKeyPress='addSelectInpKeyPress(this,event)' onClick='event.stopPropagation()' placeholder='Add item'> <span class='glyphicon glyphicon-plus addnewicon' onClick='addSelectItem(this,event,1);'></span>";

  var divider = $('<option/>')
          .addClass('divider')
          .data('divider', true);


  var addoption = $('<option/>', {class: 'addItem'})
          .data('content', content)

  $el.append(divider)
  $el.append(addoption)
  $el.selectpicker();

};

function addSelectItem(t,ev){
   ev.stopPropagation();

   var bs = $(t).closest('.bootstrap-select')
   var txt=bs.find('.bss-input').val().replace(/[|]/g,"");
   var txt=$(t).prev().val().replace(/[|]/g,"");
   if ($.trim(txt)=='') return;

   // Changed from previous version to cater to new
   // layout used by bootstrap-select.
   var p=bs.find('select');
   var o=$('option', p).eq(-2);
   o.before( $("<option>", { "selected": true, "text": txt}) );
   p.selectpicker('refresh');
}

function addSelectInpKeyPress(t,ev){
   ev.stopPropagation();

   // do not allow pipe character
   if (ev.which==124) ev.preventDefault();

   // enter character adds the option
   if (ev.which==13)
   {
      ev.preventDefault();
      addSelectItem($(t).next(),ev);
   }
}

function dynamicSort(property) {
  var sortOrder = 1;
  if(property[0] === "-") {
      sortOrder = -1;
      property = property.substr(1);
  }
  return function (a,b) {
      /* next line works with strings and numbers, 
       * and you may want to customize it to your needs
       */
      var result = (a[property] < b[property]) ? -1 : (a[property] > b[property]) ? 1 : 0;
      return result * sortOrder;
  }
}

function SetLanguage(language){

  console.log(language);

  $datasTable.bootstrapTable("filterBy", {});
  if($datasTable.bootstrapTable("getData").length > 0){
    var firstQs = $datasTable.bootstrapTable("getData")[0];
    var needInit = true;
    $.each(Object.keys(firstQs.labels), function(i, lang){
      if(lang == language){
        console.log(language + " is already set.");
        needInit = false;
      }
    });

    if(!needInit){
        $datasTable.bootstrapTable("filterBy", {});
        $.each($datasTable.bootstrapTable("getData"), function(i, qs){
          console.log(qs);
          if(qs.labels[language]){
            qs.label = qs.labels[language];
          }
          else{
            qs.label = "";
          }
          if(qs.descriptions[language]){
            qs.description = qs.descriptions[language];
          }
          else{
            qs.description = "";
          }
          $.each(qs.fields, function(j, field){
            if(field.labels[language]){
              field.label = field.labels[language];
            }
            else{
              field.label = "";
            }
            if(field.descriptions[language]){
              field.description = field.descriptions[language];
            }
            else{
              field.description = "";
            }
          })
          $.each(qs.relations, function(j, relation){
            if(relation.descriptions[language]){
              relation.description = relation.descriptions[language];
            }
            else{
              relation.description = "";
            }
            if(relation.labels[language]){
              relation.label = relation.labels[language];
            }
            else{
              relation.label = "";
            }
          })
        });
    }

    if(needInit){
      console.log("Let's initialize...");
      $datasTable.bootstrapTable("filterBy", {});
      $.each($datasTable.bootstrapTable("getData"), function(i, qs){
        qs.labels[language] = "";
        qs.descriptions[language] = "";
        qs.label = qs.labels[language];
        qs.description = qs.descriptions[language];
        $.each(qs.fields, function(j, field){
          field.labels[language] = "";
          field.descriptions[language] = "";
          field.label = field.labels[language];
          field.description = field.descriptions[language];
        })
        $.each(qs.relations, function(j, relation){
          relation.description = "";
          relation.label = "";
        })
      });

    }

    if(activeTab.match("Query Subject")){
      $refTab.tab('show');
      $qsTab.tab('show');
    }
    else{
      $qsTab.tab('show');
    }

    $("#langSelect").selectpicker('val', language);
    $("#langSelect").selectpicker('refresh');

  }

}

$('#selectDimension').change(function () {
});

$('#selectDimension').on('show.bs.select', function (e, clickedIndex, isSelected, previousValue) {
  console.log("show");
});

$('#selectDimension').on('shown.bs.select', function (e, clickedIndex, isSelected, previousValue) {
  console.log("shown");
});

$('#selectDimension').on('hide.bs.select', function (e, clickedIndex, isSelected, previousValue) {
  console.log("hide");
});

$('#selectDimension').on('hidden.bs.select', function (e, clickedIndex, isSelected, previousValue) {
  console.log("hidden");
});

$('#selectDimension').on('loaded.bs.select', function (e, clickedIndex, isSelected, previousValue) {
  console.log("loaded");
});

$('#selectDimension').on('rendered.bs.select', function (e, clickedIndex, isSelected, previousValue) {
  console.log("rendered");
});

$('#selectDimension').on('refreshed.bs.select', function (e, clickedIndex, isSelected, previousValue) {
  console.log("refreshed");
});

$('#selectDimension').on('changed.bs.select', function (e, clickedIndex, isSelected, previousValue) {
  console.log("changed");
  var selectedText = $(this).find("option:selected").val();
  if(selectedText != ''){
    $('#selectTimeDimension').selectpicker('deselectAll')
    updateDimension(selectedText);
    $("#bkExpression").prop('disabled', false);
    $("#taAttribut").prop('disabled', false);
    $('#hierarchyName').prop('disabled', false);
  }
});

$('#selectTimeDimension').on('changed.bs.select', function (e, clickedIndex, isSelected, previousValue) {
  // do something...
  if($(this).val().length > 0){
    $('#selectDimension').selectpicker('val','');
    $('#selectOrder').empty();
    $('#selectOrder').selectpicker('refresh');
    $('#selectBK').empty();
    $('#selectBK').selectpicker('refresh');
    $('#selectAttribut').empty();
    $('#selectAttribut').selectpicker('refresh');
    $("#bkExpression").val('');
    $("#bkExpression").prop('disabled', true);
    $("#taAttribut").val('');
    $("#taAttribut").prop('disabled', true);
    $('#hierarchyName').val('');
    $('#hierarchyName').prop('disabled', true);

  }
});

$("#DrillModal").on('shown.bs.modal', function(){

  var dims = $("#dimSelect option");
  // console.log(dims);

  var values = $.map(dims ,function(dim) {
      return dim.value;
  });

  // console.log(values);
  // console.log($selectedDimension);
  // console.log(Gdimensions);

  if($selectedDimension.dimension != "" && Gdimensions[$selectedDimension.dimension] != undefined){
    $('#selectTimeDimension').selectpicker('deselectAll');
    $('#selectDimension').selectpicker('val', $selectedDimension.dimension);
    $('#selectDimension').selectpicker('refresh');
    updateDimension($selectedDimension.dimension);
    if($selectedDimension.order != ""){
      var orderQsFinalName = $selectedDimension.order.split('.').slice(1,2).toString().replace(/[\[\]]/g, '');
      var orderOrder = $selectedDimension.order.split('.').slice(2,3).toString().replace(/[\[\]]/g, '');

      $('#selectOrder').selectpicker('val', orderQsFinalName + ' -- ' + orderOrder);
      $('#selectOrder').selectpicker('refresh');
    }
    else{
      $('#selectOrder').selectpicker('val', "");
      $('#selectOrder').selectpicker('refresh');
    }
    $("#bkExpression").val($selectedDimension.bk);
    $("#taAttribut").val($selectedDimension.attributs);
  }
  if($selectedDimension.dimension != "" && Gdimensions[$selectedDimension.dimension] == undefined){
    dimension = $selectedDimension.dimension.replace(/[\[\]]/g, '').split(',');
    $('#selectTimeDimension').selectpicker('val',dimension);
  }
});

function updateDimension(dimension){

  $('#selectOrder').empty();
  $('#selectOrder').selectpicker('refresh');
  $('#selectBK').empty();
  $('#selectBK').selectpicker('refresh');
  $("#bkExpression").val('');
  $('#selectAttribut').empty();
  $('#selectAttribut').selectpicker('refresh');
  $("#taAttribut").val('');

  var emptyOption = '<option class="fontsize" value="" data-subtext=""></option>';

  var orders = Gdimensions[dimension].orders;
  var bks = Gdimensions[dimension].bks;
  var alias = $('#drillFieldName').text().split('.')[0];
  $.each(orders, function(i, order){

    var icon = "";
    if(order.isPK){
      icon =  " - " + "<i class='glyphicon glyphicon-star'></i>";
    }
    if(order.isIdx && !order.isPK){
      icon = " - " + "<i class='glyphicon glyphicon-star-empty'></i>";
    }
    var label = "";
    if(order.label || order.label != ""){
      label = " - " + order.label;
    }
    var subText = icon + label + " - " + order.qsFinalName;

    var option = '<option class="fontsize" value="' + order.qsFinalName + ' -- ' + order.order + '" data-subtext="' + subText + '">' + order.order + '</option>';
    $('#selectOrder').append(option);
  })
  $('#selectOrder').append(emptyOption);
  $('#selectOrder').selectpicker('val', "");
  $('#selectOrder').selectpicker('refresh');

  $.each(bks, function(i, bk){

    var icon = "";
    if(bk.isPK){
      icon =  " - " + "<i class='glyphicon glyphicon-star'></i>";
    }
    if(bk.isIdx && !bk.isPK){
      icon = " - " + "<i class='glyphicon glyphicon-star-empty'></i>";
    }
    var label = "";
    if(bk.label || bk.label != ""){
      label = " - " + bk.label;
    }
    var subText = icon + label + " - " + bk.qsFinalName;

    var option = '<option class="fontsize" value="' + bk.qsFinalName + ' -- ' + bk.bk + '" data-subtext="' + subText + '">' + bk.bk + '</option>';
    $('#selectBK').append(option);
    $('#selectAttribut').append(option);
  })
  // if($("#selectBK option[value='']").length > 0){
  $('#selectBK').append(emptyOption);
  $('#selectBK').selectpicker('val', '');
  $('#selectAttribut').append(emptyOption);
  $('#selectAttribut').selectpicker('val', '');
  // }

  $('#selectBK').selectpicker('refresh');
  $('#selectAttribut').selectpicker('refresh');
}

function getDimensions(dimensionSet, selectedQs){

  var dimensions = [];
  var qss = {};
  dimensionSet.forEach(function(value){
    console.log(value.value);
    if(value.value != undefined && value.value != ""){
      dimensions.push(value.value);
    }
  })

  // $datasTable.bootstrapTable("filterBy", {});

  $.each($datasTable.bootstrapTable("getData"), function(i, obj){
    qss[obj._id] = obj;
  });

  var parms = {dimensions: JSON.stringify(dimensions), qss: JSON.stringify(qss), selectedQs: selectedQs};
  console.log(parms);

  $.ajax({
    type: 'POST',
    url: "GetDimensionsOptim",
    dataType: 'json',
    data: JSON.stringify(parms),
    success: function(data) {
      console.log(data);
      Gdimensions = data.DATA;
      var emptyOption = '<option class="fontsize" value="" data-subtext="' + '' + '"></option>';

      if(data.DATA != null && data.DATA){
        if(Object.keys(data.DATA).length > 0){
          $.each(Object.values(data.DATA), function(i, dimension){
            var dimensionOption = '<option class="fontsize" value="' + dimension.name + '" data-subtext="' + '' + '">' + dimension.name + '</option>';
            $('#selectDimension').append(dimensionOption);
          })
        }
      }

      $('#selectDimension').append(emptyOption);
      $('#selectDimension').selectpicker('val', "");
      $('#selectDimension').selectpicker('refresh');

      $('#DrillModal').modal('toggle');

    },
    error: function(data) {
      console.log(data);
    }
  });


}

function AddBKExpression(){

  var output = $("#bkExpression").val();
  // var order = $('#selectOrder').find("option:selected").val();
  var bk = $('#selectBK').find("option:selected").val();
  if(bk){
    // var orderQsFinalName = order.split(' -- ').slice(0,1).toString();
    var bkQsFinalName = bk.split(' -- ').slice(0,1).toString();
    // var orderOrder = order.split(' -- ').slice(1).toString();
    var bkBk = bk.split(' -- ').slice(1).toString();
  }

  if(bkQsFinalName && bkBk){
    if(output != ''){
      output += " || '-' || " + '[DATA].[' + bkQsFinalName + '].[' + bkBk + ']';
    }
    else{
      output = '[DATA].[' + bkQsFinalName + '].[' + bkBk + ']';
    }
  }

  $("#bkExpression").val(output);

}

function AddAttribut(){

  var attributList = $("#taAttribut").val();
  // var order = $('#selectOrder').find("option:selected").val();
  var option = $('#selectAttribut').find("option:selected").val();

  var QsFinalName = option.split(' -- ').slice(0,1).toString();
  var attribut = option.split(' -- ').slice(1).toString();

  console.log(attribut);

  if(QsFinalName && attribut){
    if(attributList != ''){
      attributList += ';' + '[DATA].[' + QsFinalName + '].[' + attribut + ']';
    }
    else{
      attributList = '[DATA].[' + QsFinalName + '].[' + attribut + ']';
    }
  }

  console.log(attributList);

  $('#taAttribut').val(attributList);

}

function BuildDrillPath(){

  var dimension = $('#selectDimension').find("option:selected").val();
  var order = $('#selectOrder').find("option:selected").val();
  var bk = $('#selectBK').find("option:selected").val();
  console.log(order);
  if(order && order != '' && order != undefined){
    var orderQsFinalName = order.split(' -- ').slice(0,1).toString();
    var orderOrder = order.split(' -- ').slice(1).toString();
  }
  if(bk && bk != '' && bk != undefined){
    var bkQsFinalName = bk.split(' -- ').slice(0,1).toString();
    var bkBk = bk.split(' -- ').slice(1).toString();
  }

  if($("#selectTimeDimension").val().length > 0){
    dimension = $("#selectTimeDimension").val();
    if(Array.isArray(dimension)){
      dimension = '[' + dimension.toString() + ']';
    };
  }

  var hierarchyName = $('#hierarchyName').val();

  var alias = $('#drillFieldName').text().split('.')[0];
  var field = $('#drillFieldName').text().split('.')[1];

  if($activeSubDatasTable != undefined){

    var dim = $activeSubDatasTable.bootstrapTable("getData")[$selectedDimension.index];
    if(!dimension){dim.dimension = ""} else{dim.dimension = dimension};
    if(!orderQsFinalName && !orderOrder){dim.order = ""} else{dim.order = '[DATA].[' + orderQsFinalName + '].[' + orderOrder + ']';}
    // if(!bkQsFinalName && !bkBk){dim.bk = ""} else{dim.bk = '[DATA].[' + bkQsFinalName + '].[' + bkBk + ']'};
    dim.bk = $("#bkExpression").val();
    dim.attributs = $('#taAttribut').val();
    dim.hierarchyName = hierarchyName;
    updateRow($activeSubDatasTable, $selectedDimension.index, dim);

  }

  $('#DrillModal').modal('toggle');

}

$("#ExpressionModal").on('shown.bs.modal', function(){

});

function AddExpressionData(){

  var selectedQs = $('#selectExpressionQS').find("option:selected").text();
  var selectedField = $('#selectExpressionField').find("option:selected").text();

  if(selectedQs == "*"){
    AddExpression();
    return;
  }

  if(!selectedQs || !selectedField){
    ShowAlert("Select one Query Subject and one Field.", "alert-warning", $("#ExpressionModalAlert"));
    return;    
  }

  var dataExpression;

  if(selectedQs.startsWith("[FINAL].")){
    dataExpression = selectedQs.replace("[FINAL].","[DATA].") + ".[" + selectedField + "]";
  }
  else{
    dataExpression = selectedQs;
    dataExpression = dataExpression.replace("[REF].","");
    dataExpression = dataExpression.replace("[","");
    dataExpression = dataExpression.replace("]","");
    console.log(dataExpression);

    dataExpression = "[DATA].[" + dataExpression.substr(0, dataExpression.indexOf(".")) + 
      "].[" + dataExpression.substr(dataExpression.indexOf(".") + 1) + "." + selectedField + "]";
  }

  var expression = $("#taExpression").val();

  if(expression.length > 0){
    $("#taExpression").val(expression + " " + dataExpression);
  }
  else{
    $("#taExpression").val(dataExpression);
  }
  
}

function AddExpression(){

  var selectedQs = $('#selectExpressionQS').find("option:selected").text();
  var selectedField = $('#selectExpressionField').find("option:selected").text();

  if(!selectedQs || !selectedField){
    ShowAlert("Select one Query Subject and one Field.", "alert-warning", $("#ExpressionModalAlert"));
    return;    
  }

  var expression = $("#taExpression").val();

  if(expression.length > 0){
    $("#taExpression").val(expression + " " + selectedQs + ".[" + selectedField + "]");
  }
  else{
    $("#taExpression").val(selectedQs + ".[" + selectedField + "]");
  }

}

$('#selectExpressionQS').on('changed.bs.select', function (e, clickedIndex, isSelected, previousValue) {

  $('#selectExpressionField').empty();
  $('#selectExpressionField').selectpicker('refresh');

  var qss = {};
  var selectedQs = $(this).find("option:selected").val();
  var selectedQsText = $(this).find("option:selected").text();
  console.log(selectedQs);
  var qs;

  if(selectedQs == "*"){
    selectedQs = selectedQS._id;
  }

  $.each($datasTable.bootstrapTable("getData"), function(i, obj){
    if(obj._id == selectedQs){
      qs = obj;
    }
  });

  $.each(qs.fields, function(i, field){
    var option = '<option class="fontsize" value="' + field.field_name + '" data-subtext="' + selectedQsText + '">' + field.field_name + '</option>';
    $('#selectExpressionField').append(option);
  });

  $('#selectExpressionField').selectpicker('refresh');

});

function SaveFieldsToView(){

  // console.log(fieldsView);
  var views = $('#ViewsTable').bootstrapTable("getData");
  var tables = $('#DatasTable').bootstrapTable("getData");
  var viewName = $('#addFieldsViewName').text();

  var view;

  $.each(views, function(index, obj){
    if(obj.table_name == viewName){
      view = obj;
    }
  })

  $.each(Object.values(fieldsView), function(key, field){

    $.each(tables, function(i, qs){
      if(field.qs == qs._id){
        $.each(qs.fields, function(j, qsField){
          if(field.field_name == qsField.field_name){
            var viewField = $.extend({}, qsField);
            viewField._id = field.id;
            viewField.expression = field.expression;
            viewField.fieldPos = 0;
            view.fields.push(viewField);
          }
        })
      }
    
    })
  
  })

  $("#AddfieldsModal").modal('toggle');

}


function AddFieldsToView(){

  var viewName = $('#addFieldsViewName').text();

  var selectedQs = $("#addFieldsQS").find("option:selected").val();
  var selectedQsText = $("#addFieldsQS").find("option:selected").text();
  var selectedFields = $('#addFieldsField').val();

  if(!selectedQs || !selectedFields){
    ShowAlert("Select one Query Subject and one Field at least.", "alert-warning", $("#AddfieldsModalAlert"));
    return;    
  }

  console.log(viewName);
  console.log(selectedQs);
  console.log(selectedQsText);
  console.log(selectedFields);

  // fieldsView;

  var _id = "";
  var expression = "";

  if(selectedQsText.startsWith("[FINAL].")){
    _id = selectedQsText.replace("[FINAL].","");
    _id = _id.replace("[","");
    _id = _id.replace("]","");
    expression = selectedQsText.replace("[FINAL].","[DATA].") + ".[";
    _id = "FINAL." + _id;
  }
  else{
    _id = selectedQsText;
    _id = _id.replace("[REF].","");
    _id = _id.replace("[","");
    _id = _id.replace("]","");
    expression = "[DATA].[" + _id.substr(0, _id.indexOf(".")) + 
      "].[" + _id.substr(_id.indexOf(".") + 1) + ".";
    _id = "REF." + _id;
    }

  
  $.each(selectedFields, function(i, obj){

    var field = {_id: "", id: "", qs: "", field_name: "", expression: ""};
    var temp = _id + "." + obj;
    var a = temp.split(".");
    var l = a.length;
    field.id = a[l-2] + "." + a[l-1];
    field.qs = selectedQs;
    field.field_name = obj;
    field.expression = expression + obj + "]";
    field._id = _id + "." + obj;
    fieldsView[_id + "." + obj] = field;

  })

  console.log(fieldsView);
  console.log(Object.keys(fieldsView).length);

  ShowAlert(Object.keys(fieldsView).length + " field(s) added.", "alert-success", $("#AddfieldsModalAlert"));

  
  // var dataExpression;

  // if(selectedQsText.startsWith("[FINAL].")){
  //   dataExpression = selectedQsText.replace("[FINAL].","[DATA].") + ".[" + selectedFields + "]";
  // }
  // else{
  //   dataExpression = selectedQsText;
  //   dataExpression = dataExpression.replace("[REF].","");
  //   dataExpression = dataExpression.replace("[","");
  //   dataExpression = dataExpression.replace("]","");
  //   console.log(dataExpression);

  //   dataExpression = "[DATA].[" + dataExpression.substr(0, dataExpression.indexOf(".")) + 
  //     "].[" + dataExpression.substr(dataExpression.indexOf(".") + 1) + "." + selectedFields + "]";
  // }

  // var expression = $("#addFieldsList").val();

  // if(expression.length > 0){
  //   $("#addFieldsList").val(expression + " " + dataExpression);
  // }
  // else{
  //   $("#addFieldsList").val(dataExpression);
  // }
  
}


$('#addFieldsQS').on('changed.bs.select', function (e, clickedIndex, isSelected, previousValue) {

  $('#addFieldsField').empty();
  $('#addFieldsField').selectpicker('refresh');

  var qss = {};
  var selectedQs = $(this).find("option:selected").val();
  var selectedQsText = $(this).find("option:selected").text();
  console.log(selectedQs);
  var qs;

  if(selectedQs == "*"){
    selectedQs = selectedQS._id;
  }

  $.each($datasTable.bootstrapTable("getData"), function(i, obj){
    if(obj._id == selectedQs){
      qs = obj;
    }
  });

  $.each(qs.fields, function(i, field){
    var option = '<option class="fontsize" value="' + field.field_name + '" data-subtext="' + selectedQsText + '">' + field.field_name + '</option>';
    $('#addFieldsField').append(option);
  });

  $('#addFieldsField').selectpicker('refresh');

  // var parms = {qss: JSON.stringify(qss), selectedQs: selectedQs};
  // console.log(parms);

  // $.ajax({
  //   type: 'POST',
  //   url: "GetFieldsFullPath",
  //   dataType: 'json',
  //   data: JSON.stringify(parms),
  //   success: function(data) {
  //   console.log(data);
  //   var emptyOption = '<option class="fontsize" value="" data-subtext="' + '' + '"></option>';

  //   if(data.DATAS != null && data.DATAS){
  //     if(Object.keys(data.DATAS).length > 0){
  //       // var exp = "\\[\\."
  //       // var regex = new RegExp(exp, "gi");
  //       $.each(data.DATAS, function(key, value){
  //         key = key.replace("[.", "[");
  //         var option = '<option class="fontsize" value="' + value + '" data-subtext="' + key + '">' + value + '</option>';
  //         $('#selectExpressionField').append(option);
  //       })
  //     }
  //   }

  //   $('#selectExpressionField').append(emptyOption);
  //   $('#selectExpressionField').selectpicker('val', "");
  //   $('#selectExpressionField').selectpicker('refresh');

  //   },
  //   error: function(data) {
  //     console.log(data);
  //   }
  // });


});

function DeleteFilter(){

  var selectedFilter = $('#selectFilterName').find("option:selected").val();

  if(!selectedFilter){
    ShowAlert("No filter selected.", "alert-warning", $("#FiltersModalAlert"));
    return;
  }

  var selectedOption = $('#selectFilterName option:selected');

  if(selectedQS && selectedFilter){

    bootbox.confirm({
      message: "Do you really want to delete <strong>" + selectedFilter + "</strong> ?",
      buttons: {
          confirm: {
              label: 'Yes',
              className: 'btn-primary'
          },
          cancel: {
              label: 'No',
              className: 'btn-default'
          }
      },
      callback: function (result) {
  
        if(!result){
          return;
        }

        delete selectedQS.filters[selectedFilter];
        $("#DatasTable").bootstrapTable('updateRow', {index: selectedQS.index, row: selectedQS});
        console.log($("#DatasTable").bootstrapTable('getData'));
        selectedOption.remove();
        $('#selectFilterName').selectpicker('refresh');
        $('#selectFilterName').trigger('change');
      }

    });
  }
}

function AddFilter(){

  console.log(selectedQS);

  bootbox.prompt({
    size: "small",
    title: "Enter filter name",
    callback: function(result){
      var status = 'OK';
      filterName = result;
      if(!filterName){
        return;
      }

      if(selectedQS){
        if(!jQuery.isEmptyObject(selectedQS.filters)){
          if(Object.keys(selectedQS.filters).length > 0){
            $.each(Object.keys(selectedQS.filters), function(index, key){
              console.log(key);
              if(key.toUpperCase() == filterName.toUpperCase()){
                ShowAlert(filterName + " already exists.", "alert-warning", $("#FiltersModalAlert"));
                status = 'KO';
                return;
              }
            })
          }
        }
      }

      if(status == 'OK'){

        var parms = {"filterName": filterName}; 
        console.log(parms);

        $.ajax({
          type: 'POST',
          url: "GetNewFilter",
          dataType: 'json',
          data: JSON.stringify(parms),

          success: function(data) {
            console.log(data);
            if(data.DATAS && selectedQS){
              var nameOption = '<option class="fontsize" value="' + data.DATAS.name + '" data-subtext="' + '' + '">' + data.DATAS.name + '</option>';
              $('#selectFilterName').append(nameOption);
              $('#selectFilterName').selectpicker('val', data.DATAS.name);
              $('#selectFilterName').selectpicker('refresh');
              $('#selectFilterOption').selectpicker('val', data.DATAS.option);
              $('#selectFilterOption').selectpicker('refresh');
              $('#selectFilterName').trigger('change');

              if(selectedQS.type == "Ref"){
                $('#selectFilterTarget').selectpicker('val', "*");
              }


            }
                        
          },
          error: function(data) {
              console.log(data);
          }
        });
      }

    }
  });  

}

function BuildFilter(){

  if(selectedQS){

    var name = $("#selectFilterName").find("option:selected").val();
    var option = $("#selectFilterOption").find("option:selected").val();
    var target = $("#selectFilterTarget").find("option:selected").val();
    var expression = $("#taFilterExpression").val();

    if(!name || name.length == 0){
      ShowAlert("Filter name can't be empty.", "alert-warning", $("#FiltersModalAlert"));
      return;
    }

    if(expression.length == 0){
      ShowAlert("Filter expression can't be empty.", "alert-warning", $("#FiltersModalAlert"));
      return;
    }

    var filter = {"name": name, "option": option, "target": target, "expression": expression};

    if(jQuery.isEmptyObject(selectedQS.filters)){
      console.log(selectedQS.filters);
      var filters = {};
      filters[name] = filter;
      selectedQS.filters = filters
    }
    else{
      console.log(selectedQS.filters);
      filters = selectedQS.filters;
      filters[name] = filter;
    }

    $("#DatasTable").bootstrapTable('updateRow', {index: selectedQS.index, row: selectedQS});


  }

  $('#FiltersModal').modal('toggle');

  console.log($("#DatasTable").bootstrapTable('getData'));

}

function loadSelectFilterOption(){

  $('#selectFilterOption').empty();
  var mandatoryOption = '<option class="fontsize" value="' + "Mandatory" + '" data-subtext="' + '' + '">' + "Mandatory" + '</option>';
  $('#selectFilterOption').append(mandatoryOption);
  var facultativeOption = '<option class="fontsize" value="' + "Facultative" + '" data-subtext="' + '' + '">' + "Facultative" + '</option>';
  $('#selectFilterOption').append(facultativeOption);
  $('#selectFilterOption').selectpicker('refresh');


}

function loadSelectFilterTarget(){

  $('#selectFilterTarget').empty();
  $('#selectFilterTarget').selectpicker('refresh');

  if(!selectedQS){
    return;
  }

  var qss = {};
  var selectedQs = selectedQS._id;

  $.each($datasTable.bootstrapTable("getData"), function(i, obj){
    qss[obj._id] = obj;
  });

  var parms = {qss: JSON.stringify(qss), selectedQs: selectedQs};
  console.log(parms);

  $.ajax({
    type: 'POST',
    url: "GetFilterTarget",
    dataType: 'json',
    data: JSON.stringify(parms),

    success: function(data) {
    console.log(data);
    var emptyOption = '<option class="fontsize" value="" data-subtext="' + '' + '"></option>';
    var allOption = '<option class="fontsize" value="' + "*" + '" data-subtext="All">' + "*" + '</option>';
    var isRef = true;
    var defaultValue = "*";

    if(data.DATAS != null && data.DATAS){
      if(Object.keys(data.DATAS).length > 0){
        // var exp = "\\[\\."
        // var regex = new RegExp(exp, "gi");
        $.each(data.DATAS, function(key, value){
          if(value.endsWith("Final")){
            isRef = false;
            defaultValue = key;
          }
          key = key.replace("[.", "[");
          var option = '<option class="fontsize" value="' + key + '" data-subtext="' + value + '">' + key + '</option>';
          $('#selectFilterTarget').append(option);
        })
      }
    }

    if(isRef){
      $('#selectFilterTarget').append(allOption);
    }

    var selectedFilter = $('#selectFilterName').find("option:selected").val();
    if(selectedFilter){
      console.log("...");
      var filter = selectedQS.filters[selectedFilter];
      defaultValue = filter.target;
    }

    $('#selectFilterTarget').selectpicker('val', defaultValue);
    $('#selectFilterTarget').selectpicker('refresh');

    },
    error: function(data) {
      console.log(data);
    }
  });  
}

function AddFilterExpression(){

  var selectedQS = $('#selectFilterQS').find("option:selected").text(); 
  var selectedField = $('#selectFilterField').find("option:selected").val();

  var expression = $("#taFilterExpression").val();

  if(expression.length > 0){
    $("#taFilterExpression").val(expression + " " + selectedQS + ".[" + selectedField + "]");
  }
  else{
    $("#taFilterExpression").val(selectedQS + ".[" + selectedField + "]");
  }

}

$('#selectFilterName').on('changed.bs.select', function (e, clickedIndex, isSelected, previousValue) {

  var filterName = $(this).find("option:selected").val();

  var filter = selectedQS.filters[filterName];

  if(filter){
    var name = filter.name;
    var option = filter.option;
    var target = filter.target;
    var expression = filter.expression;

    $('#selectFilterName').selectpicker('val', name);
    $('#selectFilterOption').selectpicker('val', option);
    $('#selectFilterTarget').selectpicker('val', target);
    $("#taFilterExpression").val(expression);  

    $('#selectFilterName').selectpicker('refresh');
    $('#selectFilterOption').selectpicker('refresh');;
    $('#selectFilterTarget').selectpicker('refresh');;
  }
  else{
    $("#taFilterExpression").val("");
  }

});  

$('#selectFilterQS').on('changed.bs.select', function (e, clickedIndex, isSelected, previousValue) {

  $('#selectFilterField').empty();
  $('#selectFilterField').selectpicker('refresh');

  var qss = {};
  var selectedQs = $(this).find("option:selected").val();
  var selectedQsText = $(this).find("option:selected").text();
  console.log(selectedQs);
  var qs;

  if(selectedQs == "*"){
    selectedQs = selectedQS._id;
  }

  $.each($datasTable.bootstrapTable("getData"), function(i, obj){
    if(obj._id == selectedQs){
      qs = obj;
    }
  });

  $.each(qs.fields, function(i, field){
    var option = '<option class="fontsize" value="' + field.field_name + '" data-subtext="' + selectedQsText + '">' + field.field_name + '</option>';
    $('#selectFilterField').append(option);
  });

  $('#selectFilterField').selectpicker('refresh');

  // var parms = {qss: JSON.stringify(qss), selectedQs: selectedQs};
  // console.log(parms);

  // $.ajax({
  //   type: 'POST',
  //   url: "GetFieldsFullPath",
  //   dataType: 'json',
  //   data: JSON.stringify(parms),
  //   success: function(data) {
  //   console.log(data);
  //   var emptyOption = '<option class="fontsize" value="" data-subtext="' + '' + '"></option>';

  //   if(data.DATAS != null && data.DATAS){
  //     if(Object.keys(data.DATAS).length > 0){
  //       // var exp = "\\[\\."
  //       // var regex = new RegExp(exp, "gi");
  //       $.each(data.DATAS, function(key, value){
  //         key = key.replace("[.", "[");
  //         var option = '<option class="fontsize" value="' + value + '" data-subtext="' + key + '">' + value + '</option>';
  //         $('#selectFilterField').append(option);
  //       })
  //     }
  //   }

  //   $('#selectFilterField').append(emptyOption);
  //   $('#selectFilterField').selectpicker('val', "");
  //   $('#selectFilterField').selectpicker('refresh');

  //   },
  //   error: function(data) {
  //     console.log(data);
  //   }
  // });

});


function loadSelectFilterQS(){

  $('#selectFilterQS').empty();
  $('#selectFilterQS').selectpicker('refresh');

  $('#selectFilterField').empty();
  $('#selectFilterField').selectpicker('refresh');

  var qss = {};
  $.each($datasTable.bootstrapTable("getData"), function(i, obj){
    qss[obj._id] = obj;
  });

  console.log(qss);

  $.ajax({
    type: 'POST',
    url: "GetQSFullPath",
    dataType: 'json',
    data: JSON.stringify(qss),
    success: function(data) {
    console.log(data);
    var emptyOption = '<option class="fontsize" value="" data-subtext="' + '' + '"></option>';
    var allOption = '<option class="fontsize" value="' + "*" + '" data-subtext="All">' + "*" + '</option>';
    $('#selectFilterQS').append(emptyOption);
    $('#selectFilterQS').append(allOption);

    if(data.DATAS != null && data.DATAS){
      if(Object.keys(data.DATAS).length > 0){
        $.each(data.DATAS, function(key, value){
          var option = '<option class="fontsize" value="' + value + '" data-subtext="' + value + '">' + key + '</option>';
          $('#selectFilterQS').append(option);
        })
      }
    }

    $('#selectFilterQS').selectpicker('val', "");
    $('#selectFilterQS').selectpicker('refresh');

    $("#FiltersModal").modal('toggle');

    },
    error: function(data) {
      console.log(data);
    }
  });  

}

function loadSelectFilterName(){

  $('#selectFilterName').empty();

  if(selectedQS){
    if(!jQuery.isEmptyObject(selectedQS.filters)){
      if(Object.keys(selectedQS.filters).length > 0){
        $.each(Object(selectedQS.filters), function(key, value){
          console.log(key);
          console.log(value.name);
          var option = '<option class="fontsize" value="' + value.name + '" data-subtext="' + '' + '">' + value.name + '</option>';
          $('#selectFilterName').append(option);
        })
      }
      var firstName = Object.values(selectedQS.filters)[0].name;
      var firstOption = Object.values(selectedQS.filters)[0].option;
      var firstTarget = Object.values(selectedQS.filters)[0].target;
      var firstExpression = Object.values(selectedQS.filters)[0].expression;

      $('#selectFilterName').selectpicker('val', firstName);
      $('#selectFilterOption').selectpicker('val', firstOption);
      $('#selectFilterTarget').selectpicker('val', firstTarget);
      $("#taFilterExpression").val(firstExpression);

    }
    else{
      $("#taFilterExpression").val("");
    }
  }
  $('#selectFilterName').selectpicker('refresh');
  $('#selectFilterOption').selectpicker('refresh');;
  $('#selectFilterTarget').selectpicker('refresh');;
}


function loadAddFields(){
  
  $('#addFieldsQS').empty();
  $('#addFieldsQS').selectpicker('refresh');

  $('#addFieldsField').empty();
  $('#addFieldsField').selectpicker('refresh');

  var qss = {};
  $.each($datasTable.bootstrapTable("getData"), function(i, obj){
    qss[obj._id] = obj;
  });

  console.log(qss);

  $.ajax({
    type: 'POST',
    url: "GetQSFullPath",
    dataType: 'json',
    data: JSON.stringify(qss),
    success: function(data) {
    console.log(data);
    var emptyOption = '<option class="fontsize" value="" data-subtext="' + '' + '"></option>';
    // var allOption = '<option class="fontsize" value="' + "*" + '" data-subtext="All">' + "*" + '</option>';
    $('#addFieldsQS').append(emptyOption);
    // $('#addFieldsQS').append(allOption);

    if(data.DATAS != null && data.DATAS){
      if(Object.keys(data.DATAS).length > 0){
        $.each(data.DATAS, function(key, value){
          var option = '<option class="fontsize" value="' + value + '" data-subtext="' + value + '">' + key + '</option>';
          $('#addFieldsQS').append(option);
        })
      }
    }

    $('#addFieldsQS').selectpicker('val', "");
    $('#addFieldsQS').selectpicker('refresh');
    $("#addFieldsList").val("");
    $("#AddfieldsModal").modal('toggle');
    fieldsView = {};

    },
    error: function(data) {
      console.log(data);
    }
  });

}


function loadSelectExpressionQS(){
  
  $('#selectExpressionQS').empty();
  $('#selectExpressionQS').selectpicker('refresh');

  $('#selectExpressionField').empty();
  $('#selectExpressionField').selectpicker('refresh');

  var qss = {};
  $.each($datasTable.bootstrapTable("getData"), function(i, obj){
    qss[obj._id] = obj;
  });

  console.log(qss);

  $.ajax({
    type: 'POST',
    url: "GetQSFullPath",
    dataType: 'json',
    data: JSON.stringify(qss),
    success: function(data) {
    console.log(data);
    var emptyOption = '<option class="fontsize" value="" data-subtext="' + '' + '"></option>';
    var allOption = '<option class="fontsize" value="' + "*" + '" data-subtext="All">' + "*" + '</option>';
    $('#selectExpressionQS').append(emptyOption);
    $('#selectExpressionQS').append(allOption);

    if(data.DATAS != null && data.DATAS){
      if(Object.keys(data.DATAS).length > 0){
        $.each(data.DATAS, function(key, value){
          var option = '<option class="fontsize" value="' + value + '" data-subtext="' + value + '">' + key + '</option>';
          $('#selectExpressionQS').append(option);
        })
      }
    }

    $('#selectExpressionQS').selectpicker('val', "");
    $('#selectExpressionQS').selectpicker('refresh');
    $("#ExpressionModal").modal('toggle');

    },
    error: function(data) {
      console.log(data);
    }
  });

}

function BuildExpression(){

  if($activeSubDatasTable != undefined && selectedField){
    console.log($("#taExpression").val());
    selectedField.expression = $("#taExpression").val();
    updateRow($activeSubDatasTable, selectedField.index, selectedField);

  }

  $('#ExpressionModal').modal('toggle');  
}


function aboveFormatter(value, row, index){

  if(row.seqs.length < 2){
    // row.above = row.seqs[0].column_name;
    return row.seqs[0].column_name;
  }

  else{
    return row.seqs[1].column_name;

    // row.above = row.seqs[0].column_name;
    // var options_str = "";
    // var above = row.above;
    //
    // $.each(row.seqs, function(index, seq){
    //   options_str += '<option value="' + seq.column_name + '">' + seq.column_name + '</option>';
    // });
    //
    // return [
    //   '<select class="Select1" name="drop1" id="Select1">',
    //   options_str,
    //   '</select>'
    // ].join('');
  }
}

function dimensionsFormatter (value, row, index) {
  if(row.dimensions.length > 0){
    return [
      '<a class="addRelation" href="javascript:void(0)" title="Edit Filters">',
      '<i class="glyphicon glyphicon-zoom-in"></i>',
      '</a>'
    ].join('');
}
  else{
    return '';
  }
}

function filtersFormatter (value, row, index) {
  // console.log(row);
  if(row.filters){
    if(Object.keys(row.filters).length > 0){
      return [
        '<a class="addRelation" href="javascript:void(0)" title="Edit Filters">',
        '<i class="glyphicon glyphicon-edit"></i>',
        '</a>'
      ].join('');
    }
    else{
      return '';
    }
  }
  else{
    return '';
  }
}

function expressionFormatter (value, row, index) {
  if(row.expression.length > 0){
    return [
      '<a class="addRelation" href="javascript:void(0)" title="Edit Expression">',
      '<i class="glyphicon glyphicon-edit"></i>',
      '</a>'
    ].join('');
  }
  else{
    return '';
  }
}

function operateRelationFormatter(value, row, index) {
    return [
        '<a class="duplicate" href="javascript:void(0)" title="Duplicate">',
        '<i class="glyphicon glyphicon-duplicate"></i>',
        '</a>  ',
        '<a class="remove" href="javascript:void(0)" title="Remove">',
        '<i class="glyphicon glyphicon-trash"></i>',
        '</a>'
    ].join('');
}

function operateQSFormatter(value, row, index) {
    return [
        '<a class="addRelation" href="javascript:void(0)" title="Add Relation">',
        '<i class="glyphicon glyphicon-plus-sign"></i>',
        '</a>  ',
        '<a class="expandAllQS" href="javascript:void(0)" title="Expand all QS">',
        '<i class="glyphicon glyphicon-resize-full"></i>',
        '</a>  ',
        '<a class="collapseAllQS" href="javascript:void(0)" title="Collapse all QS">',
        '<i class="glyphicon glyphicon-resize-small"></i>',
        '</a>'
    ].join('');
}

function addPKRelationFormatter(value, row, index) {
    return [
        '<a class="addPKRelation" href="javascript:void(0)" title="Add PK relation(s)">',
        '<i class="glyphicon glyphicon-magnet"></i>',
        '</a>'
    ].join('');
}

function addRelationFormatter(value, row, index) {
    return [
        '<a class="addRelation" href="javascript:void(0)" title="Add new relation">',
        '<i class="glyphicon glyphicon-plus-sign"></i>',
        '</a>'
    ].join('');
}

function addFieldFormatter(value, row, index) {
    return [
        '<a class="addField" href="javascript:void(0)" title="Add new field">',
        '<i class="glyphicon glyphicon-plus-sign"></i>',
        '</a>'
    ].join('');
}

function addFieldsFormatter(value, row, index) {
  return [
      '<a class="addFields" href="javascript:void(0)" title="Add fields">',
      '<i class="glyphicon glyphicon-plus-sign"></i>',
      '</a>'
  ].join('');
}

// function addFolderFormatter(value, row, index) {
//     return [
//         '<a class="addFolder" href="javascript:void(0)" title="Add new folder">',
//         '<i class="glyphicon glyphicon-folder-open"></i>',
//         '</a>'
//     ].join('');
// }
//
// function addDimensionNameFormatter(value, row, index) {
//     return [
//         '<a class="addDimension" href="javascript:void(0)" title="Add new dimension name">',
//         '<i class="glyphicon glyphicon-zoom-in"></i>',
//         '</a>'
//     ].join('');
// }

function addDimensionFormatter(value, row, index) {
    return [
        '<a class="addDimension" href="javascript:void(0)" title="Add new dimension">',
        '<i class="glyphicon glyphicon-plus-sign"></i>',
        '</a>'
    ].join('');
}

function hiddenFormatter(value, row, index) {

  // console.log("****** VALUE *********" + value);
  //
  // if(value == undefined){
  //   value = false;
  // }
  var icon = value == true ? 'glyphicon-check' : 'glyphicon-unchecked'
  if(value == undefined){
      // console.log("****** VALUE *********" + value);
      // console.log(row);
      icon = 'glyphicon-unchecked';
  }

  return [
    '<a href="javascript:void(0)">',
    '<i class="glyphicon ' + icon + '"></i> ',
    '</a>'
  ].join('');
}


function boolFormatter(value, row, index) {

  // console.log("****** VALUE *********" + value);
  //
  // if(value == undefined){
  //   value = false;
  // }
  var icon = value == true ? 'glyphicon-check' : 'glyphicon-unchecked'
  if(value == undefined){
      // console.log("****** VALUE *********" + value);
      // console.log(row);
      icon = 'glyphicon-unchecked';
  }
  return [
    '<a href="javascript:void(0)">',
    '<i class="glyphicon ' + icon + '"></i> ',
    '</a>'
  ].join('');
}

function duplicateFormatter(value, row, index) {
  return [
      '<a class="duplicate" href="javascript:void(0)" title="Duplicate">',
      '<i class="glyphicon glyphicon-duplicate"></i>',
      '</a>'
  ].join('');
}

function removeRootTableFormatter(value, row, index) {

// console.log(row.relations);

var relChecked = false;

$.each(row.relations, function(i, rel){
  if(rel.fin || rel.ref || rel.tra || rel.sec){
    relChecked = true;
  }
});

// console.log(relChecked);
// if(!row.linker && row.linker_ids[0] == "Root"){
  if(!relChecked){

  return [
      '<a class="remove" href="javascript:void(0)" title="Remove">',
      '<i class="glyphicon glyphicon-trash"></i>',
      '</a>'
  ].join('');
  }
  else {
    return "";
  }

}

function removeRelationFormatter(value, row, index) {

  if(!row.fin && !row.ref && !row.sec){

  return [
      '<a class="remove" href="javascript:void(0)" title="Remove">',
      '<i class="glyphicon glyphicon-trash"></i>',
      '</a>'
  ].join('');
  }
  else {
    return "";
  }

}

function removeFieldFormatter(value, row, index) {

  // console.log(row);
  // console.log(activeTab);

  if(activeTab.match("View")){
    return [
      '<a class="remove" href="javascript:void(0)" title="Remove">',
      '<i class="glyphicon glyphicon-trash"></i>',
      '</a>'
    ].join('');
  }


  if(row.custom){

  return [
      '<a class="remove" href="javascript:void(0)" title="Remove">',
      '<i class="glyphicon glyphicon-trash"></i>',
      '</a>'
  ].join('');
  }
  else {
    return "";
  }

}

function removeDimensionFormatter(value, row, index) {

  return [
      '<a class="remove" href="javascript:void(0)" title="Remove">',
      '<i class="glyphicon glyphicon-trash"></i>',
      '</a>'
  ].join('');
}


function buildDrillPathFormatter(value, row, index) {
  return [
      '<a class="buildDrillPath" href="javascript:void(0)" title="Build Drill Path">',
      '<i class="glyphicon glyphicon-zoom-in"></i>',
      '</a>'
  ].join('');
}

function indexFormatter(value, row, index) {
  row.index = index;
  return index;
}

function modComputerelation(){

  var alias = $('#modQuerySubject').text().split(" - ")[0];
  var type = $('#modQuerySubject').text().split(" - ")[1].toUpperCase();
  var table = $('#modQuerySubject').text().split(" - ")[2];
  var relations = $('#modRelationship').val();

  var exp = "\\[" + type + "\\]\.\\[" + alias + "\\]\.\\[([A-Z0-9_]*?)\\][^\\[]*?\\[([A-Z0-9_]*?)\\]\.\\[([A-Z0-9_]*?)\\]";
  // var exp = "\\s{0,}=\\s{0,}\\[(.*?)\\]\.\\[(.*?)\\]";

  var regexp = new RegExp(exp, "gi");

  var match;
  var colMatches = [];
  var pkTabMatches = [];
  var pkColMatches = [];

  while(match = regexp.exec(relations)){
    colMatches.push(match[1]);
    pkTabMatches.push(match[2]);
    pkColMatches.push(match[3]);
  };

  console.log(colMatches);
  console.log(pkTabMatches);
  console.log(pkColMatches);

  if(colMatches.length == 0){
    ShowAlert("No valid relation found in Relations textarea.<br>Relations does not match " + exp + " pattern.", "alert-warning", $("#newRowModalAlert"));
    return;
  }

  var whereClause;
  exp = "\\[" + type + "\\]\.\\[" + alias + "\\]\.";
  regexp = new RegExp(exp, "gi");
  whereClause = relations.replace(regexp, "[" + table + "].");
  whereClause = whereClause.replace(/[\[\]]/g, "");
  console.log(whereClause);


  var qs_recCount;

  var data = $datasTable.bootstrapTable("getData");

  var qs = $('#modQuerySubject').text().split(" - ")[0] + $('#modQuerySubject').text().split(" - ")[1];

  $.each(data, function(i, obj){
    //console.log(obj.name);
    if(obj._id.match(qs)){
      qs_recCount = obj.recCount;
    }
  });

  console.log(qs_recCount);

  regexp = new RegExp("[^\\.^=^ ]*\\.[^\\.^=^ ]*", "gi");
  var cols = whereClause.match(regexp);
  console.log(cols);

  var tables = [];
  $.each(cols, function(i, col){
    var table = col.split(".")[0];
    tables.push(table);
  })

  console.log(tables);

  var set = new Set();
  $.each(tables, function(i, table){
    set.add(table);
  })

  console.log(set);

  var tableClause = Array.from(set).join(', ');

  console.log(tableClause);

  var query = "SELECT COUNT(*) as COUNT, (cast(round((count(*)/" + qs_recCount + ".0)*100, 3) as numeric(31,3))) as PERCENT FROM " + tableClause + " WHERE " + whereClause;

  console.log(query);
  var parms = {query: query};

  $.ajax({
    type: 'POST',
    url: "GetSQLQuery",
    dataType: 'json',
    data: JSON.stringify(parms),

    success: function(data) {
      if(data.STATUS == "OK"){
        console.log(data);
        $("#recCount").text(data.result[0].COUNT);
        var percent = data.result[0].PERCENT;
        if(percent == 100){
          $("#recCountPercent").removeClass("label-warning").removeClass("label-danger").addClass("label-success").text(data.result[0].PERCENT);
        }
        if(percent == 0){
          $("#recCountPercent").removeClass("label-success").removeClass("label-warning").addClass("label-danger").text(data.result[0].PERCENT);
        }
        if(percent != 0 && percent != 100){
          $("#recCountPercent").removeClass("label-success").removeClass("label-danger").addClass("label-warning").text(data.result[0].PERCENT);
        }
      }
      else{
        console.log(data);
        ShowAlert("ERROR: " + data.MESSAGE + "<br>" + data.EXCEPTION + "<br>TROUBLESHOOTING: " + data.TROUBLESHOOTING, "alert-danger", $("#newRowModalAlert"));
      }
    },
    error: function(data) {
      console.log(data);
    }
  });


}

$('#modRelationship').on('input selectionchange propertychange', function() {
  emptyRecCount();
});

$("#butBuildRelation").click(function(){
  emptyRecCount();
})

function modEraseRelation(){
  $('#modRelationship').val("");
  emptyRecCount();
}

function emptyRecCount(){
  $("#recCount").text("");
  $("#recCountPercent").text("");
}

function modBuildRelation(){

  if(!validNewRelation()){
    return;
  }

  var relations = $('#modRelationship');
  var alias = $('#modQuerySubject').text().split(" - ")[0];
  var type = $('#modQuerySubject').text().split(" - ")[1].toUpperCase();
  var table = $('#modQuerySubject').text().split(" - ")[2];
  var pktable = $('#modPKTable').find("option:selected").val();
  var column = $('#modColumn').find("option:selected").val();
  var pkcolumn = $('#modPKColumn').find("option:selected").val();

  var output = relations.val();
  if(relations.val() != ''){
    output += ' AND ';
  }
  output += '[' + type + '].[' + alias + '].[' + column + '] = [' + pktable + '].[' + pkcolumn + ']';

  relations.val(output);

}

function modAddRelation(){

  var alias = $('#modQuerySubject').text().split(" - ")[0];
  var table = $('#modQuerySubject').text().split(" - ")[2];
  var type = $('#modQuerySubject').text().split(" - ")[1].toUpperCase();
  var key_type = $('#modKeyType').val();
  var relations = $('#modRelationship').val();

  var exp = "\\[" + type + "\\]\.\\[" + alias + "\\]\.\\[([A-Z0-9_]*?)\\][^\\[]*?\\[([A-Z0-9_]*?)\\]\.\\[([A-Z0-9_]*?)\\]";
  // var exp = "\\s{0,}=\\s{0,}\\[(.*?)\\]\.\\[(.*?)\\]";

  var regexp = new RegExp(exp, "gi");

  var match;
  var colMatches = [];
  var pkTabMatches = [];
  var pkColMatches = [];

  while(match = regexp.exec(relations)){
    colMatches.push(match[1]);
    pkTabMatches.push(match[2]);
    pkColMatches.push(match[3]);
  };

  console.log(colMatches);
  console.log(pkTabMatches);
  console.log(pkColMatches);

  if(colMatches.length == 0){
    ShowAlert("No valid relation found in Relations textarea.<br>Relations does not match " + exp + " pattern.", "alert-warning", $("#newRowModalAlert"));
    return;
  }

  var parms = {"table": pkTabMatches[0]};

  $.ajax({
      type: 'POST',
      url: "GetNewRelation",
      dataType: 'json',
      data: JSON.stringify(parms),

      success: function(data){
        console.log(data);
        if(data.STATUS == "OK"){
          var relation = data.DATAS;

          $.each(colMatches, function(i, obj){
            var seq = {};
            seq.key_seq = i + 1;
            seq.table_name = table;
            seq.pktable_name = pkTabMatches[i];
            seq.column_name = colMatches[i]
            seq.pkcolumn_name = pkColMatches[i];
            relation.seqs.push(seq);
          })

          $.each(relation.seqs, function(i, seq){
            if(relation.where != ''){
              relation.where += ' AND ';
            }
            relation.where += seq.table_name + '.' + seq.column_name + ' = ' + seq.pktable_name + '.' + seq.pkcolumn_name;
          })

          relation.relationship = relations;
          relation.type = type;
          relation.table_name = table;
          relation.table_alias = alias;
          relation.pktable_name = pkTabMatches[0];
          relation.pktable_alias = pkTabMatches[0];
          relation.key_type = key_type;
          relation.key_name = key_type + 'K_' + alias + '_' + pkTabMatches[0];
          relation._id = relation.key_name + '_' + type;
          relation.above = relation.seqs[0].column_name;
          relation.recCount = $("#recCount").text();
          relation.recCountPercent = $("#recCountPercent").text();

          modWriteRelation(relation);
        }
        else{
          ShowAlert("Error when getting new relation from server.", "alert-warning", $("#newRowModalAlert"));
        }
      },
      error: function(data) {
          console.log(data);
          ShowAlert("Error when getting new relation from server.", "alert-warning", $("#newRowModalAlert"));
      }
  });

}

function modWriteRelation(relation){

  var data = $datasTable.bootstrapTable("getData");

  var qs = $('#modQuerySubject').text().split(" - ")[0] + $('#modQuerySubject').text().split(" - ")[1];

  $.each(data, function(i, obj){
    //console.log(obj.name);
    if(obj._id.match(qs)){
      if(obj.relations.length == 0){
        obj.relations.push(relation);
      }
    }
  });

  if($activeSubDatasTable){
    AddRow($activeSubDatasTable, relation);
  }

  $newRowModal.modal('toggle');

}

function validNewRelation(){

  if ($("#modPKTable").find("option:selected").text() == 'Choose a pktable...') {
    ShowAlert("No pktable selected.", "alert-warning", $("#newRowModalAlert"));
    return false;
  }

  if ($("#modPKColumn").find("option:selected").text() == 'Choose a pkcolumn...') {
    ShowAlert("No pkcolumn selected.", "alert-warning", $("#newRowModalAlert"));
    return false;
  }

  if ($("#modColumn").find("option:selected").text() == 'Choose a column...') {
    ShowAlert("No column selected.", "alert-warning", $("#newRowModalAlert"));
    return false;
  }

  return true;
}

function Search(){
  if(currentProject){
    console.log(currentProject.resource.jndiName);
    if(currentProject.resource.jndiName == "XML"){
      // promptTeasing();  
      window.open("search.html");
    }
    else{
      window.open("search.html");
    }
  }
 
}

function getSetFromArray(array){
  var result = new Set();
  $.each(array, function(i, obj){
    result.add(obj);
  })
  return result;
}

function getArrayFromSet(set){
  var result = [];
  set.forEach(function(value){
    result.push(value);
  });
  return result;
}

function clearDrillModal(){
  $('#selectTimeDimension').selectpicker('deselectAll');
  $('#selectDimension').empty();
  $('#selectDimension').selectpicker('refresh');
  $('#selectOrder').empty();
  $('#selectOrder').selectpicker('refresh');
  $('#selectBK').empty();
  $('#selectBK').selectpicker('refresh');
  $('#drillFieldName').text('');
  $('#hierarchyName').text('');
  $('#bkExpression').val('');
}

function expandRelationTable($detail, cols, data, qs) {
    $subtable = $detail.html('<table></table>').find('table');
    $activeSubDatasTable = $subtable;
    buildRelationTable($subtable, cols, data, qs);
}

function expandDimensionTable($detail, cols, data, field, qs) {
    $subtable = $detail.html('<table></table>').find('table');
    $activeSubDatasTable = $subtable;
    buildDimensionTable($subtable, cols, data, field, qs);
}

function expandFieldTable($detail, cols, data, qs) {
    $subtable = $detail.html('<table></table>').find('table');
    $activeSubDatasTable = $subtable;
    buildFieldTable($subtable, cols, data, qs);
}

function buildDimensionTable($el, cols, data, fld, qs){

  $el.bootstrapTable({
      columns: cols,
      // url: url,
      data: data,
      showToggle: false,
      search: false,
      checkboxHeader: false,
      showColumns: false,
      // sortName: "recCountPercent",
      // sortOrder: "desc",
      idField: "index",

      onAll: function(name, args){
        //Fires when all events trigger, the parameters contain: name: the event name, args: the event data.
        console.log(name);
        console.log(args);
      },

      onPreBody: function(data){
        console.log(data);
        if(data.length > 0){
          $.each(data, function(index, dimension){
            if(!dimension.attributs){
              dimension.attributs = '';
            }
          })
        }
      },

      onEditableInit: function(){
        //Fired when all columns was initialized by $().editable() method.
      },
      onEditableShown: function(editable, field, row, $el){
        //Fired when an editable cell is opened for edits.
      },
      onEditableHidden: function(field, row, $el, reason){
        //Fired when an editable cell is hidden / closed.
      },
      onEditableSave: function (field, row, oldValue, editable) {
        //Fired when an editable cell is saved.

        if(field == "dimension"){
          var dimension = row.dimension;
          if(Array.isArray(dimension)){
            row.dimension = '[' + dimension.toString() + ']';
          };
        }

      },
      onResetView: function(){
        // var $tableRows = $el.find('tbody tr');
        //
        // $.each(data, function(i, row){
        //   console.log(fld);
        //   console.log(qs);
        //   console.log($tableRows.eq(i).find('a'));
          // $tableRows.eq(i).find('a').eq(0).editable('enable');

          // Change dimension to checklist editable if field_type are time/date and remove zoom-in icon
          // $tableRows.eq(i).find('a').eq(0) = dimension
          // if(fld.timeDimension){
          //   row.order = '';
          //   row.bk = '';
          //   row.hierarchyName = '';
          //   $tableRows.eq(i).find('a').eq(1).editable('disable');
          //   $tableRows.eq(i).find('a').eq(2).editable('disable');
          //   $tableRows.eq(i).find('a').eq(3).editable('disable');
          //   $tableRows.eq(i).find('a').eq(0).editable('destroy');
          //   $tableRows.eq(i).find('a').eq(0).editable(dateDimensions);
          //   $tableRows.eq(i).find('a.buildDrillPath').remove();
          // }
          // else{
            // $tableRows.eq(i).find('a').eq(0).editable('destroy');
            // var dimensionSet = getSetFromArray(dimensionGlobal);
            //
            // var source = [];
            // source.push({"text": "", "value": ""});
            //
            // dimensionSet.forEach(function(value){
            //   var option = {};
            //   option.text = value;
            //   option.value = value;
            //   source.push(option);
            // })
            //
            // var newEditable = {
            //   type: "select",
            //   mode: "inline",
            //   source: source
            // };
            //
            // $tableRows.eq(i).find('a').eq(0).editable(newEditable);
            // $tableRows.eq(i).find('a').eq(0).editable('option', 'defaultValue', '');
          // }
        // });

      },

      onClickCell: function (field, value, row, $element){

        $activeSubDatasTable = $el;

        switch(field){

          case "dimension":
            break;

          case "remove":
            $el.bootstrapTable('remove', {
                field: 'index',
                values: [row.index]
            });
            return;

          case "buildDrillPath":

            if(field.timeDimension){
              return;
            }

            //Check if QS is either Final, RefChecked or Ref
            qsType = {isFinal: false, isRefChecked: false, isRef: false};

            $.each($datasTable.bootstrapTable("getData"), function(i, obj){
              if(obj.table_alias == qs.table_alias){
                if(obj.type == 'Final'){
                  qsType.isFinal = true;
                  $.each(obj.relations, function(j, relation){
                    if(relation.ref){
                      qsType.isRefChecked = true;
                    }
                  });
                }
                if(obj.type != 'Final'){
                      qsType.isRef = true;
                }
              }
            });

            clearDrillModal();

            var fieldName = qs.table_alias + '.' + fld.field_name;
            $('#drillFieldName').text(fieldName);

            var source = [];

            $("#dimSelect option").each(function(){
              var option = {};
              option.text = $(this).val();
              option.value = $(this).val();
              source.push(option);
            });

            // var options = $("#dimSelect option");
            // var values = $.map(options, function(option){
            //   return option.value;
            // })

            var dimensionSet = getSetFromArray(source);

            $selectedDimension = row;

            getDimensions(dimensionSet, qs._id);

            break;

          default:

        }
      }

    });

}

function buildFieldTable($el, cols, data, qs){

  console.log(cols);
  console.log(activeTab);
  var detailView = true;

  if(activeTab.match("Query Subject")){
    cols = $.grep(cols, function(el, idx) {return el.field == "folder"}, true);
    cols = $.grep(cols, function(el, idx) {return el.field == "alias"}, true);
    cols = $.grep(cols, function(el, idx) {return el.field == "role"}, true);
  }

  if(activeTab.match("View")){
    cols = $.grep(cols, function(el, idx) {return el.field == "traduction"}, true);
    cols = $.grep(cols, function(el, idx) {return el.field == "measure"}, true);
    cols = $.grep(cols, function(el, idx) {return el.field == "dimensions"}, true);
    cols = $.grep(cols, function(el, idx) {return el.field == "addDimension"}, true);
    cols = $.grep(cols, function(el, idx) {return el.field == "displayType"}, true);
    detailView = false;
  }

  console.log(cols);

      $el.bootstrapTable({
          columns: cols,
          // url: url,
          data: data,
          showToggle: false,
          search: false,
          checkboxHeader: false,
          showColumns: false,
          // sortName: "recCountPercent",
          // sortOrder: "desc",
          idField: "index",
          detailView: detailView,

          onExpandRow: function (index, row, $detail) {
              console.log(index);
              console.log(row);
              console.log($detail);
              console.log($el);
              var $tableRows = $el.find('tbody td');
              console.log($tableRows.eq(0));
              expandDimensionTable($detail, dimensionCols, row.dimensions, row, qs);
          },

          onAll: function(name, args){
            //Fires when all events trigger, the parameters contain: name: the event name, args: the event data.
          },

          onEditableInit: function(){
            //Fired when all columns was initialized by $().editable() method.
          },
          onEditableShown: function(editable, field, row, $el){
            //Fired when an editable cell is opened for edits.
          },
          onEditableHidden: function(field, row, $el, reason){
            //Fired when an editable cell is hidden / closed.
          },
          onEditableSave: function (field, row, oldValue, editable) {
            //Fired when an editable cell is saved.
            console.log(row);
            var currentLanguage = $('#langSelect').find("option:selected").val();
            if(field.match("label")){
              row.labels[currentLanguage] = row.label;
            }
            if(field.match("description")){
              row.descriptions[currentLanguage] = row.description;
            }
            console.log(row);

          },

          onPreBody: function(data){
            //Fires before the table body is rendered, the parameters contain: data: the rendered data.
            console.log(data);
            if(data.length > 0){
              $.each(data, function(index, dimension){
                if(!dimension.attributs){
                  dimension.attributs = '';
                }
              })
            }
          },

          onPostBody: function(data){
            // Fires after the table body is rendered and available in the DOM, the parameters contain: data: the rendered data.
        },

          onResetView: function(){

            var $tableRows = $el.find('tbody tr');


            $.each(data, function(i, row){

              // Disable editable for field_type if field is !custom and remove trash
              // $tableRows.eq(i).find('a').eq(0) = field_type
              if(activeTab.match("Query Subject") && $activeSubDatasTable == $el){
                if(!row.custom){
                  // $tableRows.eq(i).find('a').eq(1).editable('destroy');
                  $tableRows.eq(i).find('a').eq(1).editable('disable');
                  $tableRows.eq(i).find('a.remove').remove();
                }
                else{
                  $tableRows.eq(i).find('a').eq(1).editable('destroy');
                  customFieldType.source = dbDataType;
                  $tableRows.eq(i).find('a').eq(1).editable(customFieldType);
                  $tableRows.eq(i).find('a').eq(1).editable('option', 'defaultValue', '');
                }
              }

            })

          },

          onClickCell: function (field, value, row, $element){

            $activeSubDatasTable = $el;

            switch(field){

              case "addDimension":

                if($('#dimSelect option').length == 1){
                  showalert("", 'No dimension created yet.', "alert-warning", "bottom");
                  $("#addDim").trigger('click');
                  return;
                }

                var dimension = {dimension: '', order: '', bk: '', attributs: '', hierarchyName: ''};
                row.dimensions.push(dimension);
                $el.bootstrapTable("expandRow", row.index);
                $el.bootstrapTable("collapseRow", row.index);
                $el.bootstrapTable("expandRow", row.index);

                break;

                case "traduction":
                  var newValue = value == false ? true : false;
                  updateCell($el, row.index, field, newValue);
                  break;                    $el.bootstrapTable('remove', {
                    field: 'index',
                    values: [row.index]
                });


              case "hidden":
                var newValue = value == false ? true : false;
                updateCell($el, row.index, field, newValue);
                break;

              case "remove":
                if(activeTab.match("Query Subject")){
                  if(row.custom == true){
                    $el.bootstrapTable('remove', {
                        field: 'index',
                        values: [row.index]
                    });
                  }
                }
                if(activeTab.match("View")){
                  $el.bootstrapTable('remove', {
                      field: 'index',
                      values: [row.index]
                  });
                }
                return;

              case "expression":
                console.log(row);
                var fieldName = qs._id + "." + row.field_name;
                $("#ExpressionFieldName").text(fieldName);
                $("#taExpression").val(row.expression);
                selectedField = row;
                selectedQS = qs;
                loadSelectExpressionQS();

                break;

              default:

            }

          }

      });

}

function buildLabelsMap(){

  var result = {};
  var currentLanguage = $('#langSelect').find("option:selected").val();

  $.each($datasTable.bootstrapTable("getData"), function(i, qs){
    result[qs._id.toUpperCase()] = qs.labels[currentLanguage];
    result[qs._id.toUpperCase()].description = qs.descriptions[currentLanguage];
  });

  console.log(result);

  return result;

}

function buildDescriptionsMap(){

  var result = {};
  var currentLanguage = $('#langSelect').find("option:selected").val();

  $.each($datasTable.bootstrapTable("getData"), function(i, qs){
    result[qs._id.toUpperCase()] = qs.descriptions[currentLanguage];
  });

  console.log(result);

  return result;

}

function buildRelationTable($el, cols, data, qs){

  $el.bootstrapTable({
      columns: cols,
      // url: url,
      data: data,
      showToggle: false,
      search: false,
      checkboxHeader: false,
      showColumns: false,
      // sortName: "recCountPercent",
      // sortOrder: "desc",
      idField: "index",

      onAll: function(name, args){
        //Fires when all events trigger, the parameters contain: name: the event name, args: the event data.
      },

      onEditableInit: function(){
        //Fired when all columns was initialized by $().editable() method.
      },
      onEditableShown: function(editable, field, row, $el){
        //Fired when an editable cell is opened for edits.
      },
      onEditableHidden: function(field, row, $el, reason){
        //Fired when an editable cell is hidden / closed.
      },
      onEditableSave: function (field, row, oldValue, editable) {
        //Fired when an editable cell is saved.

        row._id = row.key_type + 'K_' + row.pktable_alias + '_' + row.table_alias + '_' + row.type;
        if(field == "pktable_alias"){
          var newValue = row.pktable_alias;
          if($activeSubDatasTable != undefined){
            var re = new RegExp("[^.]\\[" + oldValue + "\\]", "gi");
            var re = new RegExp("\\[" + oldValue + "\\]", "gi");
            // updateCell($activeSubDatasTable, row.index, 'relationship', row.relationship.split(" [" + oldValue + "]").join(" [" + newValue + "]"));
            updateCell($activeSubDatasTable, row.index, 'relationship', row.relationship.replace(re, "[" + newValue + "]"));
          }
        }

      },

      onPreBody: function(data){
        //Fires before the table body is rendered, the parameters contain: data: the rendered data.
        // if(data.length > 0){
        //   $.each(data, function(i, rel){
        //     console.log(rel);
        //     var qsId = (rel.pktable_alias + rel.type).toUpperCase();
        //     var label = labelsMap[qsId];
        //     if(label){
        //       rel.label = label;
        //     }
        //     var description = descriptionsMap[qsId];
        //     if(description){
        //       rel.description = description;
        //     }
        //   });
        // }
      },

      onPostBody: function(data){
        // Fires after the table body is rendered and available in the DOM, the parameters contain: data: the rendered data.
      },

      onResetView: function(){

        var $tableRows = $el.find('tbody tr');

        $.each(data, function(i, row){

          // uncomment to trace i
          // console.log($tableRows.eq(i).find('a'));

          // If more than one seq change above from text to select editable
          // $tableRows.eq(i).find('a').eq(4) = above
          if(activeTab.match("Reference|Security") && row.seqs.length > 0){
            $tableRows.eq(i).find('a').eq(4).editable('destroy');
            // $tableRows.eq(i).find('a').eq(0).editable('setValue', ['VARCHAR']);
            var defaultValue = '';
            var source = [];
            $.each(row.seqs, function(k, seq){
              var option = {};
              option.text = seq.column_name;
              option.value = seq.column_name;
              source.push(option);
              defaultValue = seq.column_name;
            })

            customFieldType.source = source;

            $tableRows.eq(i).find('a').eq(4).editable(customFieldType);
            $tableRows.eq(i).find('a').eq(4).editable('option', 'defaultValue', defaultValue);
          }

          if(activeTab.match("Translation")){
            $tableRows.eq(i).find('a').eq(4).editable('destroy');
            // $tableRows.eq(i).find('a').eq(0).editable('setValue', ['VARCHAR']);
            var defaultValue = '';
            var source = [];

            var table_name = row.table_name

            $.each($datasTable.bootstrapTable("getData"), function(i, qs){
              if(qs.table_name == table_name){
                $.each(qs.fields, function(j, field){
                  var option = {};
                  option.text = field.field_name;
                  option.value = field.field_name;
                  source.push(option);
                  defaultValue = field.field_name;
                })
              }
            });

            customFieldType.source = source;

            $tableRows.eq(i).find('a').eq(4).editable(customFieldType);
            $tableRows.eq(i).find('a').eq(4).editable('option', 'defaultValue', defaultValue);
          }

          // set usedForDimensionsSelect.source
          // $tableRows.eq(i).find('a').eq(6) = usedForDimensions
          // if(activeTab == "Reference" && qs.type == 'Final'){
          //
          //   var dimensionSet = getSetFromArray(dimensionGlobal);
          //
          //   var source = [];
          //   source.push({"text": "", "value": ""});
          //   dimensionSet.forEach(function(value){
          //     var option = {};
          //     option.text = value;
          //     option.value = value;
          //     source.push(option);
          //   })
          //
          //   usedForDimensionsSelect.source = source;
          //
          //   $tableRows.eq(i).find('a').eq(6).editable('destroy');
          //   $tableRows.eq(i).find('a').eq(6).editable(usedForDimensionsSelect);
          //   $tableRows.eq(i).find('a').eq(6).editable('option', 'defaultValue', '');
          //
          // }

          // set usedForDimensionsSelect.source
          // $tableRows.eq(i).find('a').eq(6) = usedForDimensions
          if(activeTab == "Reference" && qs.type == 'Ref'){

            var source = [];
            source.push({"text": "", "value": ""});
            source.push({"text": "true", "value": "true"});
            source.push({"text": "false", "value": "false"});

            usedForDimensionsSelect.source = source;

            $tableRows.eq(i).find('a').eq(7).editable('destroy');
            $tableRows.eq(i).find('a').eq(7).editable(usedForDimensionsSelect);
            $tableRows.eq(i).find('a').eq(7).editable('option', 'defaultValue', '');

          }

          // Disable RepTableName if !ref or !sec
          // $tableRows.eq(i).find('a').eq(3) = RepTableName
          // if(activeTab.match("Reference|Security|Translation")){
          //   if(!row.ref || !row.sec || !row.tra){
          //     $tableRows.eq(i).find('a').eq(3).editable('disable');
          //     // To disable all editables
          //     // $tableRows.eq(i).find('a').editable('disable');
          //   }
          // }

          // disable table_alias if fin, ref, sec or tra checked
          // $tableRows.eq(i).find('a').eq(0) = table_alias
          if(activeTab.match("Final|Reference|Security|Translation")){
            if(row.fin || row.ref || row.sec || row.tra){
              $tableRows.eq(i).find('a').eq(0).editable('disable');
              // To disable all editables
              // $tableRows.eq(i).find('a').editable('disable');
            }
          }

          if(activeTab.match("Reference|Security|Translation")){
            if(row.fin){
              $tableRows.eq(i).find('a').eq(2).editable('disable');
            }
          }

          if(activeTab.match("Final|Reference|Translation")){
            if(row.sec){
              $tableRows.eq(i).find('a').eq(2).editable('disable');
            }
          }

          if(activeTab.match("Final|Security|Translation")){
            if(row.ref){
              $tableRows.eq(i).find('a').eq(2).editable('disable');
            }
          }

          if(activeTab.match("Final|Reference|Security")){
            if(row.tra){
              $tableRows.eq(i).find('a').eq(2).editable('disable');
            }
          }

        })

      },

      onClickCell: function (field, value, row, $element){

        $activeSubDatasTable = $el;

        switch(field){

          case "usedForDimensions":
            if($('#dimSelect option').length == 1){
              showalert("buildRelationTable()", 'No dimension created yet. Create one clicking <i class="glyphicon glyphicon-zoom-in"></i> in Query Subject tab.', "alert-warning", "bottom");
              return;
            }

            break;

          case "traduction":
          case "timezone":
          case "leftJoin":
          case "rightJoin":
            var newValue = value == false ? true : false;
            updateCell($el, row.index, field, newValue);
            break;

          case "relationship":
            console.log("on a clické sur relationship");
            console.log(value);
            console.log(row);
            console.log(field);

            // var alias = $('#modQuerySubject').text().split(" - ")[0];
            // var type = $('#modQuerySubject').text().split(" - ")[1].toUpperCase();
            // var table = $('#modQuerySubject').text().split(" - ")[2];
          
            // $newRowModal.modal('toggle');
            // $('#modQuerySubject').text(qs);
            // ChooseField($('#modColumn'), row.table_name);            
            break;

          case "nommageRep":
            var allowNommageRep = true;

            // if(!row.ref && activeTab.match("Reference")){
            //   allowNommageRep = false;
            //   showalert("buildRelationTable()", "Ref for " + row.pktable_alias + " has to be checked first.", "alert-warning", "bottom");
            //   return;
            // }
            // if(!row.sec && activeTab.match("Security")){
            //   allowNommageRep = false;
            //   showalert("buildRelationTable()", "Sec for " + row.pktable_alias + " has to be checked first.", "alert-warning", "bottom");
            //   return;
            // }

            // if(!row.tra && activeTab.match("Translation")){
            //   allowNommageRep = false;
            //   showalert("buildRelationTable()", "Tra for " + row.pktable_alias + " has to be checked first.", "alert-warning", "bottom");
            //   return;
            // }

            if(value == false){
              // interdire de cocher n fois pour un même pkAlias dans un qs donné
              $.each($el.bootstrapTable("getData"), function(i, obj){
                if(obj.pktable_alias == row.pktable_alias){
                  if(obj.sec && activeTab.match("Security") && obj.nommageRep){
                    allowNommageRep = false;
                  }
                  if(obj.ref && activeTab.match("Reference") && obj.nommageRep){
                    allowNommageRep = false;
                  }
                }
              });

            }
            var newValue = value == false ? true : false;
            updateCell($el, row.index, field, newValue);

            break;

          case "duplicate":

            $el.bootstrapTable("filterBy", {});
            nextIndex = row.index + 1;
            var newRow = $.extend({}, row);
            newRow.checkbox = false;
            newRow.pktable_alias = "";
            newRow.fin = false;
            newRow.ref = false;
            newRow.sec = false;
            newRow.tra = false;
            newRow.relationship = newRow.relationship.replace(/\s{1,}=\s{1,}\[FINAL\]\./g, " = ");
            newRow.relationship = newRow.relationship.replace(/\s{1,}=\s{1,}\[REF\]\./g, " = ");
            newRow.relationship = newRow.relationship.replace(/\s{1,}=\s{1,}\[SEC\]\./g, " = ");
            newRow.relationship = newRow.relationship.replace(/\s{1,}=\s{1,}\[TRA\]\./g, " = ");
            newRow.relationship = newRow.relationship.split("[" + row.pktable_alias + "]").join("[]");
            newRow.nommageRep = false;
            if(newRow.key_type == "F"){
              newRow._id = "FK_" + newRow.pktable_alias + "_" + row.table_alias + '_' +row.type;
              // newRow._id = newRow.key_name + "F";
            }
            if(newRow.key_type == "P"){
              newRow._id = "PK_" + newRow.pktable_alias + "_" + row.table_alias + '_' +row.type;
              // newRow._id = newRow.key_name + "P";
            }
            $el.bootstrapTable('insertRow', {index: nextIndex, row: newRow});
            return;

          case "remove":
            if(activeTab.match("Final|Reference|Security")){
              if(!row.fin && !row.ref){
                $el.bootstrapTable('remove', {
                    field: 'index',
                    values: [row.index]
                });
              }
              else{
                showalert("buildSubTable()", row._id + " is checked.", "alert-warning", "bottom");
              }
            }
            return;

          case "fin":
          case "ref":
          case "sec":
          case "tra":

            if(row.ref && activeTab.match("Final|Security|Translation")){
              showalert("buildSubTable()", row._id + " is already checked as REF.", "alert-warning", "bottom");
              return;
            }
            if(row.fin && activeTab.match("Reference|Security|Translation")){
              showalert("buildSubTable()", row._id + " is already checked as FINAL.", "alert-warning", "bottom");
              return;
            }
            if(row.sec && activeTab.match("Reference|Final|Translation")){
              showalert("buildSubTable()", row._id + " is already checked as SEC.", "alert-warning", "bottom");
              return;
            }
            if(row.tra && activeTab.match("Reference|Final|Security")){
              showalert("buildSubTable()", row._id + " is already checked as TRA.", "alert-warning", "bottom");
              return;
            }
            if(row.pktable_alias == ""){
              showalert("buildSubTable()", "Empty is not a valid pktable_alias.", "alert-warning", "bottom");
              return;
            }


            var newValue = value == false ? true : false;
            var pkAlias = '[' + row.pktable_alias + ']';
            if(value == true){

              var qssBackup = JSON.parse(JSON.stringify($datasTable.bootstrapTable("getData")));
              updateCell($el, row.index, field, newValue);
              UncheckQuerySubject(qssBackup);
              if(activeTab == "Final"){
                row.relationship = row.relationship.split("[FINAL]." + pkAlias).join(pkAlias);
                row.fin = false;
              }
              if(activeTab == "Reference"){
                row.relationship = row.relationship.split("[REF]." + pkAlias).join(pkAlias);
                row.ref = false;
              }
              if(activeTab == "Security"){
                row.relationship = row.relationship.split("[SEC]." + pkAlias).join(pkAlias);
                row.sec = false;
              }

              if(activeTab == "Translation"){
                row.relationship = row.relationship.split("[TRA]." + pkAlias).join(pkAlias);
                row.tra = false;
              }
            return;

              PrepareRemoveKeys(row, qs);
              if(qs2rm.qsList.length > 0){

                RemoveKeys(row, qs);
                ChangeIcon(row, qs, "Attribute");
                return;
              }
              else{
                if(activeTab == "Final"){
                  row.relationship = row.relationship.split("[FINAL]." + pkAlias).join(pkAlias);
                  row.fin = false;
                }
                if(activeTab == "Reference"){
                  row.relationship = row.relationship.split("[REF]." + pkAlias).join(pkAlias);
                  row.ref = false;
                }
                if(activeTab == "Security"){
                  row.relationship = row.relationship.split("[SEC]." + pkAlias).join(pkAlias);
                  row.sec = false;
                }

                if(activeTab == "Translation"){
                  row.relationship = row.relationship.split("[TRA]." + pkAlias).join(pkAlias);
                  row.tra = false;
                }

                var linked = false;
                $.each(qs.relations, function(i, obj){
                  if(obj.fin || obj.ref || obj.sec){
                    linked = true;
                  }
                });
                updateCell($datasTable, qs.index, "linker", linked);

              }

            }
            if(value == false){

              var re = new RegExp("[^\\.]\\[" + row.pktable_alias + "\\]", "gi");
              var re = new RegExp("\\[" + row.pktable_alias + "\\]", "gi");

              console.log(row);

              if(!row.fin && activeTab == "Final"){
                row.relationship = row.relationship.replace(re, "[FINAL].[" + row.pktable_alias + "]");
              }
              if(!row.ref && activeTab == "Reference"){
                row.relationship = row.relationship.replace(re, "[REF].[" + row.pktable_alias + "]");
              }
              if(!row.sec && activeTab == "Security"){
                row.relationship = row.relationship.replace(re, "[SEC].[" + row.pktable_alias + "]");
              }
              if(!row.tra && activeTab == "Translation"){
                row.relationship = row.relationship.replace(re, "[TRA].[" + row.pktable_alias + "]");
              }
              updateCell($el, row.index, field, newValue);
              ChangeIcon(row, qs, "Identifier");
              if(row.fin && activeTab == "Final"){
                console.log(row.pktable_name);
                console.log(row.pktable_alias);
                console.log("Final");
                console.log(row._id);
                console.log(qs.index);
                GetQuerySubjects(row.pktable_name, row.pktable_alias, "Final", row._id, qs.index);
              }
              if(row.ref && activeTab == "Reference"){
                GetQuerySubjects(row.pktable_name, row.pktable_alias, "Ref", row._id, qs.index);
              }
              if(row.sec && activeTab == "Security"){
                GetQuerySubjects(row.pktable_name, row.pktable_alias, "Sec", row._id, qs.index);
              }
              if(row.tra && activeTab == "Translation"){
                GetQuerySubjects(row.pktable_name, row.pktable_alias, "Tra", row._id, qs.index);
              }
              updateCell($datasTable, qs.index, "linker", true);

            }
            var linked = false;
            $.each(qs.relations, function(i, obj){
              if(obj.fin || obj.ref || obj.sec || obj.tra){
                linked = true;
              }
            });
            updateCell($datasTable, qs.index, "linker", linked);

            break;

          default:

        }

      }

  });

  if(activeTab == "Reference"){
    $el.bootstrapTable('hideColumn', 'fin');
    $el.bootstrapTable('showColumn', 'ref');
    $el.bootstrapTable('hideColumn', 'sec');
    $el.bootstrapTable('hideColumn', 'tra');
    $el.bootstrapTable('showColumn', 'nommageRep');
    $el.bootstrapTable('showColumn', 'usedForDimensions');
    $el.bootstrapTable('showColumn', 'rightJoin');
    $el.bootstrapTable('showColumn', 'above');
  }

  if(activeTab == "Security"){
    $el.bootstrapTable('hideColumn', 'fin');
    $el.bootstrapTable('hideColumn', 'ref');
    $el.bootstrapTable('hideColumn', 'tra');
    $el.bootstrapTable('showColumn', 'sec');
    $el.bootstrapTable('showColumn', 'nommageRep');
    $el.bootstrapTable('hideColumn', 'usedForDimensions');
    $el.bootstrapTable('hideColumn', 'rightJoin');
    $el.bootstrapTable('showColumn', 'above');
  }

  if(activeTab == "Translation"){
    $el.bootstrapTable('hideColumn', 'fin');
    $el.bootstrapTable('hideColumn', 'ref');
    $el.bootstrapTable('hideColumn', 'sec');
    $el.bootstrapTable('showColumn', 'tra');
    $el.bootstrapTable('showColumn', 'nommageRep');
    $el.bootstrapTable('hideColumn', 'usedForDimensions');
    $el.bootstrapTable('hideColumn', 'rightJoin');
    $el.bootstrapTable('showColumn', 'above');
  }

  if(activeTab == "Final"){
    $el.bootstrapTable("filterBy", {key_type: 'F'});
    $el.bootstrapTable('hideColumn', 'ref');
    $el.bootstrapTable('hideColumn', 'sec');
    $el.bootstrapTable('hideColumn', 'tra');
    $el.bootstrapTable('showColumn', 'fin');
    $el.bootstrapTable('hideColumn', 'nommageRep');
    $el.bootstrapTable('hideColumn', 'above');
    $el.bootstrapTable('hideColumn', 'usedForDimensions');
    $el.bootstrapTable('showColumn', 'rightJoin');
  }

}

function ChangeIcon(row, qs, icon){
  $.each(row.seqs, function(i, seq){
    var column_name = seq.column_name;
    $.each(qs.fields, function(j, field){
      if(field.field_name == column_name){
        field.icon = icon;
      }
    })
  })
}

$("#removeKeysModal").on('hidden.bs.modal', function (e) {
  // do something...
  if(qs2rm != undefined && $activeSubDatasTable != undefined){
    var pkAlias = '[' + qs2rm.row.pktable_alias + ']';
    if(activeTab == "Final"){
      qs2rm.row.relationship = qs2rm.row.relationship.split(pkAlias).join("[FINAL]." + pkAlias);
     updateCell($activeSubDatasTable, qs2rm.row.index, "fin", true);
    }
    if(activeTab == "Reference"){
      qs2rm.row.relationship = qs2rm.row.relationship.split(pkAlias).join("[REF]." + pkAlias);
     updateCell($activeSubDatasTable, qs2rm.row.index, "ref", true);
    }
    if(activeTab == "Security"){
      qs2rm.row.relationship = qs2rm.row.relationship.split(pkAlias).join("[SEC]." + pkAlias);
     updateCell($activeSubDatasTable, qs2rm.row.index, "sec", true);
    }
    if(activeTab == "Translation"){
      qs2rm.row.relationship = qs2rm.row.relationship.split(pkAlias).join("[TRA]." + pkAlias);
     updateCell($activeSubDatasTable, qs2rm.row.index, "tra", true);
    }
  }
})

function UncheckQuerySubject(qssBackup){

  console.log(qssBackup);

  $datasTable.bootstrapTable("filterBy", {});
  var qss = $datasTable.bootstrapTable("getData");
  
  console.log($datasTable.bootstrapTable("getData"));

  var parms = {"qss": JSON.stringify(qss)};
  console.log(parms);

  $.ajax({
    type: 'POST',
    url: "UncheckQuerySubject",
    dataType: 'json',
    data: JSON.stringify(parms),

    success: function(data) {
      console.log(data);
      if(data.DATAS != null && data.DATAS.length > 0){
        var list = '<ul class="list-group">';
        $.each(data.DATAS, function(i, qs){
          list += '<li class="list-group-item">' + qs + '</li>';
        });
        list += '</ul>';

        bootbox.confirm({
          title: "Following Query Subject will be dropped: ",
          message: list,
          buttons: {
            confirm: {
              label: 'Drop',
              className: 'btn btn-primary'
          },
          cancel: {
                label: 'Cancel',
                className: 'btn btn-default'
            }
          },
          callback: function(result){
            if(result){
              console.log("OK Kate !");
              $datasTable.bootstrapTable('remove', {
                field: '_id',
                values: data.DATAS
              });
      
            }
            else{
              console.log("KO Kate !");
              // updateCell($el, index, field, false);
              $datasTable.bootstrapTable("load", qssBackup);
            }
          }
        });        

      }
    },
    error: function(data) {
        console.log(data);
    }
  });

}

function PrepareRemoveKeys(o, qs){

        // RemoveFilter();
        // console.log($datasTable.bootstrapTable("getOptions"))
        $datasTable.bootstrapTable("filterBy", {});
        var indexes2rm = [];
        var row = o;
        var ids2rm = {};

        var recurse = function(o){
                var tableData = $datasTable.bootstrapTable("getData");
                tableData.forEach(function(e){
                        if(e.linker_ids.indexOf(o._id) > -1){
                                if(e.linker_ids.length == 1){
                                  $.each(e.relations, function(k, v){
                                    if(v.fin || v.ref || v.sec || v.tra){
                                      return recurse(v);
                                    }
                                  });
                                  indexes2rm.push(e._id);
                                }
                                if(e.linker_ids.length > 1){
                                        ids2rm[e._id] = ids2rm[e._id] || [];
                                        ids2rm[e._id].push(o._id);
                                        e.linker_ids.splice(e.linker_ids.indexOf(o._id), 1);
                                        var newValue = e.linker_ids;
                                }
                        }
                        else {
                                return;
                        }
                });
        };
        recurse(o);

        qs2rm.qs = qs;
        qs2rm.row = row;
        qs2rm.qsList = indexes2rm;
        qs2rm.ids2rm = ids2rm;

}

function RemoveKeys(row, qs){

  var list = '<ul class="list-group">';
  $.each(qs2rm.qsList, function(i, qs){
    list += '<li class="list-group-item">' + qs + '</li>';
  });
  list += '</ul>';

  bootbox.confirm({
    title: "Following Query Subject will be dropped: ",
    message: list,
    buttons: {
      confirm: {
        label: 'Drop',
        className: 'btn btn-primary'
    },
    cancel: {
          label: 'Cancel',
          className: 'btn btn-default'
      }
    },
    callback: function(result){
      if(result){
        var pkAlias = '[' + row.pktable_alias + ']';
        if(activeTab == "Final"){
          qs2rm.row.relationship = qs2rm.row.relationship.split("[FINAL]." + pkAlias).join(pkAlias);
          qs2rm.row.fin = false;
        }
        if(activeTab == "Reference"){
          qs2rm.row.relationship = qs2rm.row.relationship.split("[REF]." + pkAlias).join(pkAlias);
          qs2rm.row.ref = false;
        }
        if(activeTab == "Security"){
          qs2rm.row.relationship = qs2rm.row.relationship.split("[SEC]." + pkAlias).join(pkAlias);
          qs2rm.row.sec = false;
        }
        if(activeTab == "Translation"){
          qs2rm.row.relationship = qs2rm.row.relationship.split("[TRA]." + pkAlias).join(pkAlias);
          qs2rm.row.tra = false;
        }

        $datasTable.bootstrapTable('remove', {
          field: '_id',
          values: qs2rm.qsList
        });

        var linked = false;
        $.each(qs.relations, function(i, obj){
          if(obj.fin || obj.ref || obj.sec || obj.tra){
            linked = true;
          }
        });
        updateCell($datasTable, qs.index, "linker", linked);

      }
    }
  });

}

function RemoveKeysAccepted(){
  if(qs2rm != undefined && $activeSubDatasTable != undefined){
    var pkAlias = '[' + qs2rm.row.pktable_alias + ']';
    if(activeTab == "Final"){
      // qs2rm.row.relationship = qs2rm.row.relationship.split("[FINAL]." + pkAlias).join(pkAlias);
     updateCell($activeSubDatasTable, qs2rm.row.index, "fin", false);
    }
    if(activeTab == "Reference"){
      // qs2rm.row.relationship = qs2rm.row.relationship.split("[REF]." + pkAlias).join(pkAlias);
     updateCell($activeSubDatasTable, qs2rm.row.index, "ref", false);
    }
    if(activeTab == "Security"){
      // qs2rm.row.relationship = qs2rm.row.relationship.split("[REF]." + pkAlias).join(pkAlias);
     updateCell($activeSubDatasTable, qs2rm.row.index, "sec", false);
    }
    if(activeTab == "Translation"){
      // qs2rm.row.relationship = qs2rm.row.relationship.split("[REF]." + pkAlias).join(pkAlias);
     updateCell($activeSubDatasTable, qs2rm.row.index, "tra", false);
    }

    var linked = false;
    $.each(qs2rm.qs.relations, function(i, obj){
      if(obj.fin || obj.ref || obj.sec || obj.tra){
        linked = true;
      }
    });
    updateCell($datasTable, qs2rm.qs.index, "linker", linked);

    $datasTable.bootstrapTable('remove', {
      field: '_id',
      values: qs2rm.qsList
    });
    qs2rm.removed = true;

  }
  $("#removeKeysModal").modal('toggle');
}

function buildTable($el, cols, data) {

  console.log(data);

    $el.bootstrapTable({
        columns: cols,
        // url: url,
        data: data,
        search: false,
				showRefresh: false,
				showColumns: false,
				showToggle: false,
				pagination: false,
				showPaginationSwitch: false,
        idField: "index",
				// toolbar: "#DatasToolbar",
        detailView: true,

        onAll: function(name, args){
          //Fires when all events trigger, the parameters contain: name: the event name, args: the event data.
        },

        onEditableInit: function(){
          //Fired when all columns was initialized by $().editable() method.
        },
        onEditableShown: function(editable, field, row, $el){
          //Fired when an editable cell is opened for edits.
        },
        onEditableHidden: function(field, row, $el, reason){
          //Fired when an editable cell is hidden / closed.
        },

        onEditableSave: function (field, row, oldValue, editable) {
          //Fired when an editable cell is saved.
          var currentLanguage = $('#langSelect').find("option:selected").val();
          if(field.match("label")){
            row.labels[currentLanguage] = row.label;
            $.each($datasTable.bootstrapTable('getData'), function(i, qs){
              $.each(qs.relations, function(j, relation){
                if(relation.pktable_alias == qs.table_alias){
                  relation.label = row.label;
                }
              })
            })
          }
          if(field.match("description")){
            row.descriptions[currentLanguage] = row.description;
            $.each($datasTable.bootstrapTable('getData'), function(i, qs){
              $.each(qs.relations, function(j, relation){
                if(relation.pktable_alias == qs.table_alias){
                  relation.description = row.description;
                }
              })
            })
          }
        },

        onPreBody: function(data){
          //Fires before the table body is rendered, the parameters contain: data: the rendered data.
        },

        onPostBody: function(data){
          // Fires after the table body is rendered and available in the DOM, the parameters contain: data: the rendered data.
        },

        onResetView: function(){

          var $tableRows = $el.find('tbody tr');

          $.each($el.bootstrapTable("getData"), function(i, row){

              if(activeTab.match("Final") && $activeSubDatasTable == $el){
                if(row.linker_ids){
                  if(row.linker_ids[0].match("Root") && !row.linker){
                  }
                  else{
                    $tableRows.eq(i).find('a.remove').remove();
                  }
                }
              }
          })

        },

        onCheck: function(row, $element){
          console.log(row);
          console.log($element);
        },

        onClickCell: function (field, value, row, $element){

          $activeSubDatasTable = $el

          if(field.match("filters")){
            console.log("filters was clicked");
            console.log(row);
            $("#FiltersQSName").text(row._id);
            selectedQS = row;
            loadSelectFilterQS();
            loadSelectFilterTarget();
            loadSelectFilterOption();
            loadSelectFilterName()        
          }

          if(field.match('folder') && $('#foldSelect option').length == 1){
            showalert("", 'No folder created yet.', "alert-warning", "bottom");
            $("#addFold").trigger('click');
            // $("#foldSelect").selectpicker('toggle');
            return;
          }

          if(activeTab.match("Final")){
            if(field.match("remove")){

              console.log(row.relations);
              var alias = row.table_alias;
              console.log(alias);

              var relChecked = false;
              
              $.each(row.relations, function(i, rel){
                if(rel.fin || rel.ref || rel.tra || rel.sec){
                  relChecked = true;
                }
              });
              
              console.log(relChecked);
              if(!relChecked){
                  $el.bootstrapTable("filterBy", {});
                  $el.bootstrapTable('remove', {
                    field: 'index',
                    values: [row.index]
                  });
                  $el.bootstrapTable("filterBy", {type: 'Final'});
              }

              $.each($datasTable.bootstrapTable('getData'), function(i, qs){
                $.each(qs.relations, function(j, relation){
                  if(relation.pktable_alias == alias && relation.type == "FINAL"){
                    relation.fin = false;
                  }
                })
              })
  
              // $("#DatasTable").bootstrapTable('expandAllRows');
              $("#DatasTable").bootstrapTable('expandRow', 0);
              $refTab.tab('show');
              $finTab.tab('show');

        
              // if(row.linker_ids){
              //   if(row.linker_ids[0].match("Root") && !row.linker){
              //     $el.bootstrapTable("filterBy", {});
              //     $el.bootstrapTable('remove', {
              //       field: 'index',
              //       values: [row.index]
              //     });
              //     $el.bootstrapTable("filterBy", {type: 'Final'});
              //   }
              // }
            }
          }

          if(field == "visible"){
            var newValue = value == false ? true : false;
            updateCell($el, row.index, field, newValue);

          }

          if(field.match("addRelation")){
            $el.bootstrapTable("collapseAllRows")
            $el.bootstrapTable('expandRow', row.index);

            if($activeSubDatasTable != undefined){
              emptyRecCount();
              $newRowModal.modal('toggle');
              if(row.label){
                var qs = row.table_alias + ' - ' + row.type + ' - ' + row.table_name + ' - ' + row.label;
              }
              else{
                var qs = row.table_alias + ' - ' + row.type + ' - ' + row.table_name;
              }
              // $('#modQuerySubject').selectpicker('val', qs);

              $('#modQuerySubject').text(qs);
              ChooseField($('#modColumn'), row._id);
            }
          }
          if(field.match("addPKRelation")){
            $el.bootstrapTable('expandRow', row.index);
            GetPKRelations(row.table_name, row.table_alias, row.type);
          }

          if(field == "addFields"){
            console.log("addFields was clicked");
            console.log(row);
            var viewName = row.table_alias;
            $('#addFieldsViewName').text(viewName);
            loadAddFields();
          }

          if(field == "addField"){
            $el.bootstrapTable("collapseAllRows")
            $el.bootstrapTable('expandRow', row.index);

            if($activeSubDatasTable != undefined){
              GetNewField($activeSubDatasTable);
            }
          }

          if(field.match("addFolder")){
            AddNewFolder();
          }

          if(field.match("root")){
            console.log("root was clicked !!!");
            var newValue = value == false ? true : false;
            updateCell($el, row.index, field, newValue);

          }

          if(field.match("addDimension")){
            // AddNewDimension();

            bootbox.prompt({
              size: "small",
              title: "Enter dimension name",
              callback: function(result){
                 /* result = String containing user input if OK clicked or null if Cancel clicked */
                 if(result != null){
                   dimensionGlobal.push(result);
                   $refTab.tab('show');
                   $qsTab.tab('show');
                   $el.bootstrapTable('expandRow', row.index);
                 }

              }
            });

          }

        },
        onExpandRow: function (index, row, $detail) {
          if(activeTab.match("Final|Reference|Security|Translation")){
            expandRelationTable($detail, relationCols, row.relations, row);
          }
          else{
            expandFieldTable($detail, fieldCols, row.fields, row);
          }
        }
    });

    // $el.bootstrapTable('hideColumn', 'checkbox');
    $el.bootstrapTable('hideColumn', 'visible');
    $el.bootstrapTable('hideColumn', 'filter');
    $el.bootstrapTable('hideColumn', 'secFilter');
    $el.bootstrapTable('showColumn', 'label');
    $el.bootstrapTable('hideColumn', 'recurseCount');
    $el.bootstrapTable('hideColumn', 'addPKRelation');
    $el.bootstrapTable('hideColumn', 'addFolder');
    $el.bootstrapTable('hideColumn', 'folder');
    $el.bootstrapTable('hideColumn', 'addDimensionName');
    $el.bootstrapTable('hideColumn', 'addDimension');
    $el.bootstrapTable('showColumn', 'addField');
    $el.bootstrapTable('hideColumn', 'addFields');
    $el.bootstrapTable('hideColumn', 'merge');
    $el.bootstrapTable('showColumn', '_id');
    $el.bootstrapTable('hideColumn', 'linker');
    $el.bootstrapTable('hideColumn', 'linker_ids');
    // $el.bootstrapTable('hideColumn', 'linker');
    // $el.bootstrapTable('hideColumn', 'linker_ids');

    // $("#foldInputGroup").hide().addClass('hidden');
    // $("#foldInputGroup").hide().addClass('show');
    // if(!$("#dimInputGroup").hasClass('hidden')){
      // $("#dimInputGroup").hide().addClass('hidden');
    // }
    // $("#dimInputGroup").hide().addClass('show');
    // if(!$("#hierInputGroup").hasClass('hidden')){
      // $("#hierInputGroup").hide().addClass('hidden');
    // }
    // $("#hierInputGroup").hide().addClass('show');



    console.log("in buildTable: activeTab="+activeTab);
    console.log("in buildTable: previousTab="+previousTab);

    if(activeTab == "Reference"){
    }

    if(activeTab == "Final"){
    }

    if(activeTab == "Query Subject"){
    }

    // ApplyFilter();

}


function AddNewFolder() {

  bootbox.prompt({
    size: "small",
    title: "Enter folder name",
    callback: function(result){
       /* result = String containing user input if OK clicked or null if Cancel clicked */
      if(result != null){
        folderGlobal.push(result);
        $refTab.tab('show');
        $qsTab.tab('show');
      }
    }
  });

}

function AddNewDimension() {

  bootbox.prompt({
    size: "small",
    title: "Enter dimension name",
    callback: function(result){
       /* result = String containing user input if OK clicked or null if Cancel clicked */
       if(result != null){
         dimensionGlobal.push(result);
         $refTab.tab('show');
         $qsTab.tab('show');
       }

    }
  });

}




function GetNewField($el) {

  var fieldName;
  var rows = $el.bootstrapTable('getData');
  var nextIndex = $el.bootstrapTable("getData").length;


  bootbox.prompt({
    size: "small",
    title: "Enter field name",
    callback: function(result){
       /* result = String containing user input if OK clicked or null if Cancel clicked */
       var status = 'OK';
      fieldName = result;
      if(!fieldName){
        return;
      }

      $.each(rows, function(index, row){
        if(row.field_name.toUpperCase() == fieldName.toUpperCase()){
          showalert("GetNewField()", fieldName.toUpperCase() + " already exists.", "alert-warning", "bottom");
          status = 'KO'
  				return;
        }
      })
      if(status == 'OK'){
        $.ajax({
          type: 'POST',
          url: "GetNewField",
          dataType: 'json',

          success: function(data) {
            console.log(data);
            data._id = fieldName.toUpperCase();
            data.field_name = fieldName.toUpperCase();
            data.custom = true;
            var currentLanguage = $('#langSelect').find("option:selected").val();
            console.log(currentLanguage);
            data.labels[currentLanguage] = '';
            data.descriptions[currentLanguage] = '';
            if(activeTab.match("View")){
              data.role = "Folder"
            }
            data.fieldPos = nextIndex + 1;
            AddRow($el, data);
          },
          error: function(data) {
              console.log(data);
          }
        });
      }

    }
  });


}

function updateCell($table, index, field, newValue){

  console.log($table);
  console.log(index);
  console.log(field);
  console.log(newValue);

  $table.bootstrapTable("updateCell", {
    index: index,
    field: field,
    value: newValue
  });

}

function updateRow($table, index, row){

  $table.bootstrapTable("updateRow", {
    index: index,
    row: row
  });

}

function AddRow($table, row){

  $table.bootstrapTable("filterBy", {});
	nextIndex = $table.bootstrapTable("getData").length;
	console.log("nextIndex=" + nextIndex);
	$table.bootstrapTable('insertRow', {index: nextIndex, row: row});

}

function GetQuerySubjectsWithPK(){
  GetQuerySubjects(null, null, null, true);
}

function GetPKRelations(table_name, table_alias, type){

  var importLabel = false;
  if($('#langSelect').find("option:selected").val() == currentProject.languages[0]){
    importLabel = true;
  }

  var parms = "table=" + table_name + "&alias=" + table_alias + "&type=" + type + "&importLabel=" + importLabel;

	console.log("calling GetPKRelations with: " + parms);


  $.ajax({
    type: 'POST',
    url: "GetPKRelations",
    dataType: 'json',
    data: parms,

    success: function(data) {
			console.log(data);
      if(data.DATAS){
  			if (data.DATAS.length == 0) {
  				showalert("Relation(s) retrieved from " + data.MODE + ".<br>" + table_name + " has no PK.", "", "alert-info", "bottom");
  				return;
  			}
      }

    

      if(data.STATUS == "OK" && data.DATAS.length > 0){
        if($activeSubDatasTable != undefined){

          $('#PKModal').modal('toggle');
          // $("#PKList").selectpicker("val", emptyOption);
          $("#PKList").selectpicker("refresh");

          var index;
          var datas = $datasTable.bootstrapTable("getData");
          $.each(datas, function(i, obj){
            if(obj._id == table_alias + type){
              index = i;
            }
          })
          pKeys["QSIndex"] = index;
          var relations = datas[index].relations;

          $('#PKList').empty();
          $.each(data.DATAS, function(i, obj){
            var option = '<option class="fontsize" value="' + obj._id + '" data-subtext="(' + obj.recCountPercent + '%) ' + obj.relationship + '">' + obj.key_name + '</option>';
            $('#PKList').append(option);
            pKeys[obj._id] = obj;

            // $datasTable.bootstrapTable("getData")[index].relations.push(obj);
          });
          $("#PKList").selectpicker("refresh");

          console.log(index);

          // $datasTable.bootstrapTable("collapseRow", index);
          // $datasTable.bootstrapTable("expandRow", index);
          // showalert(data.DATAS.length + " PK relation(s) successfully retrieved from " + data.MODE + ".", "", "alert-success", "bottom");

        }
      }
      else{
        showalert("GetPKRelations()", 'Operation failed.<br>' + data.EXCEPTION + "<br>" + data.MESSAGE + '<br><br><strong><span class="glyphicon glyphicon-info-sign" aria-hidden="true"></span> ' + data.TROUBLESHOOTING + "</strong>", "alert-danger", "bottom");
      }

  	},
      error: function(data) {
          console.log(data);
          showalert("GetPKRelations()", "Operation failed.", "alert-danger", "bottom");
    }

  });

}

$("#watchContent").click(function(){
  var table_name = $tableList.find("option:selected").val();
  if(!table_name == ""){
  var query = "select * from " + table_name;
  console.log(query);
  var parms = {query: query};
  localStorage.setItem('SQLQuery', JSON.stringify(parms));
  window.open("watchContent.html");
  }
  else{
    showalert("No Table selected.", "Select a table first.", "alert-warning", "bottom");
    $("#tables").selectpicker("toggle");
  }
})

$("#addPK").click(function(){
	var pks = ($("#PKList").val());
	if(pks && pKeys){
    console.log(pks);
    console.log(pKeys);
    var index = pKeys["QSIndex"];
    $.each(pks, function(i, pk){
      $datasTable.bootstrapTable("getData")[index].relations.push(pKeys[pk]);
    });
		$('#PKModal').modal('toggle');
    $datasTable.bootstrapTable("collapseRow", index);
    $datasTable.bootstrapTable("expandRow", index);
    showalert(pks.length + " PK relation(s) successfully retrieved.", "", "alert-success", "bottom");
    pKeys = {};
	}
})

function GetQuerySubjects(table_name, table_alias, type, linker_id, index) {

	var table_name, table_alias, type, linker_id;

  if(linker_id == undefined){
    linker_id = "Root";
  }

  console.log(table_name);

	if (table_name == undefined){
		table_name = $tableList.find("option:selected").val();
	}

  console.log("table_name=" + table_name)

  if (table_name == 'Choose a table...' || table_name == '') {
		showalert("GetQuerySubjects()", "No table selected.", "alert-warning", "bottom");
		return;
	}

	if(table_alias == undefined){
		table_alias = table_name;
	}

  var a = $tableList.find("option:selected").val();
  var b = $('#alias').val();

  if(b != "" && b != a){
    table_alias = $('#alias').val();
    console.log(table_alias);
  }

	if(type == undefined){
		type = 'Final';
	}

  var qsAlreadyExist = false;

  $.each($datasTable.bootstrapTable("getData"), function(i, obj){
		//console.log(obj.name);
    if(obj._id == table_alias + type){
      qsAlreadyExist = true;
      var newValue = obj.linker_ids;
      newValue.push(linker_id);
      updateCell($datasTable, i, "linker_ids", newValue);

      showalert("GetQuerySubjects()", table_alias + type + " already exists.", "alert-info", "bottom");
    }

  });

  if(qsAlreadyExist){
    return;
  }

  var importLabel = false;
  if($('#langSelect').find("option:selected").val() == currentProject.languages[0]){
    importLabel = true;
  }

	var parms = "table=" + table_name + "&alias=" + table_alias + "&type=" + type + "&linker_id=" + linker_id + "&importLabel=" + importLabel;

	console.log("calling GetQuerySubjects() with: " + parms);

  $.ajax({
    type: 'POST',
    url: "GetQuerySubjects",
    dataType: 'json',
    data: parms,

    success: function(data) {
			console.log(data);
      if(data.STATUS == "OK"){
        var newQS = [];
        newQS.push(data.DATAS);
    		$datasTable.bootstrapTable('append', newQS);
        var datas = $datasTable.bootstrapTable("getData");
        $datasTable.bootstrapTable('expandRow', index);

        // console.log(datas);

        $("#qsSelect").empty();
        $("#qsSelect").append(emptyOption);
        $.each(datas, function(i, data){
          if(data.type.match("Final|Ref")){
            var option = '<option class="fontsize" value="' + data._id + '" data-subtext="' + data.type + '" data-content="">' + data._id + '</option>';
            $("#qsSelect").append(option);
          }
        })
        $("#qsSelect").selectpicker('refresh');
        if (data.DATAS.relations.length == 0) {
          showalert("Relation(s) retrieved from " + data.MODE + ".<br>" + table_alias + " has no key.", "", "alert-info", "bottom");
        }
        else{
          showalert(data.DATAS.relations.length + " relation(s) successfully retrieved from " + data.MODE + ".", "", "alert-success", "bottom");
        }
      }
      else{
        if(index){
          $.each($datasTable.bootstrapTable("getData")[index].relations, function(i, rel){
            if(rel.pktable_alias.match(table_alias)){
              switch(type){
                case 'Final':
                  rel.fin = false;
                  break;
                case 'Ref':
                  rel.ref = false;
                  break;
                case 'Sec':
                  rel.sec = false;
                  break;
                case 'Tra':
                  rel.tra = false;
                  break;
                default:
              }
            }
          })
          $datasTable.bootstrapTable('expandRow', index);
        }

        showalert(data.FROM, 'Operation failed.<br>' + 
        '<br><br><strong><span class="glyphicon glyphicon-info-sign" aria-hidden="true"></span> ' + 
        data.TROUBLESHOOTING + "</strong>", "alert-danger", "bottom");
      }

  	},
      error: function(data) {
          if(index){
            $.each($datasTable.bootstrapTable("getData")[index].relations, function(i, rel){
              if(rel.pktable_alias.match(table_alias)){
                switch(type){
                  case 'Final':
                    rel.fin = false;
                    break;
                  case 'Ref':
                    rel.ref = false;
                    break;
                  case 'Sec':
                    rel.sec = false;
                    break;
                  case 'Tra':
                    rel.tra = false;
                    break;
                  default:
                }
              }
            })
            $datasTable.bootstrapTable('expandRow', index);
          }

          showalert("GetQuerySubjects()", "Operation failed.", "alert-danger", "bottom");

    }

  });

  $('#alias').val("");

}

function RemoveFilter(){
  $datasTable.bootstrapTable("filterBy", {});
  if($activeSubDatasTable != undefined){
    $activeSubDatasTable.bootstrapTable("filterBy", {});
  }
}

function ApplyFilter(){
  if(activeTab == 'Final'){
    $datasTable.bootstrapTable("filterBy", {type: 'Final'});
    if($activeSubDatasTable != undefined){
      $activeSubDatasTable.bootstrapTable("filterBy", {key_type: 'F'});
    }
  }
}

function ChooseQuerySubject(table) {

	table.empty();

  var data = $datasTable.bootstrapTable("getData");

  $.each(data, function(i, obj){
		//console.log(obj.name);
		table.append('<option class="fontsize">' + obj._id + ' - ' + obj.table_name +'</option>');
  });
  table.selectpicker('refresh');

}

function SortOnStats(){

  bootbox.prompt({
      title: "Sort tables list.",
      inputType: 'select',
      inputOptions: [
          {
              text: 'Sort by...',
              value: '',
          },
          {
              text: '...alphabetic table name (ASC).',
              value: '0',
          },
          {
              text: '...number of fields within primary key (DESC).',
              value: '1',
          },
          {
              text: '...number of primary keys imported (DESC).',
              value: '2',
          },
          {
              text: '...number of sequence within primary keys imported (DESC).',
              value: '3',
          },
          {
              text: '...number of foreign keys exported (DESC).',
              value: '4',
          },
          {
              text: '...number of sequence within foreign keys exported (DESC).',
              value: '5',
          },
          {
              text: '...number of indexed fields (DESC).',
              value: '6',
          },
          {
              text: '...number of records (DESC).',
              value: '7',
          },
          {
              text: '...number of fields (DESC).',
              value: '8',
          }          
      ],
      callback: function (result) {
          ChooseTable($tableList, result);
          ChooseTable($('#modPKTable'), result);
      }
  });

}

function ChooseTable(table, sort) {

  table.empty();

  $.ajax({
    type: 'POST',
    url: "GetDBMDFromCache",
    dataType: 'json',
    async: true,
    success: function(data) {
      console.log(data);
      if(data.DATAS && !jQuery.isEmptyObject(data.DATAS)){
        if(Object.keys(data.DATAS).length > 0){
          dbmd = data.DATAS;
          var tables = Object.values(dbmd);

          console.log(tables);
          if(!sort){
            sort = "3";
            console.log('Default to sort ' + sort);
          }

          switch(sort){
            case "0":
              tables.sort(function(a, b){
                return a.table_name.localeCompare(b.table_name);
              });
              break;
            case "1":
              tables.sort(function(a, b){return b.table_primaryKeyFieldsCount - a.table_primaryKeyFieldsCount});
              break;
            case "2":
              tables.sort(function(a, b){return b.table_importedKeysCount - a.table_importedKeysCount});
              break;
            case "3":
              tables.sort(function(a, b){return b.table_importedKeysSeqCount - a.table_importedKeysSeqCount});
              break;
            case "4":
              tables.sort(function(a, b){return b.table_exportedKeysCount - a.table_exportedKeysCount});
              break;
            case "5":
              tables.sort(function(a, b){return b.table_exportedKeysSeqCount - a.table_exportedKeysSeqCount});
              break;
            case "6":
              tables.sort(function(a, b){return b.table_indexesCount - a.table_indexesCount});
              break;
            case "7":
              tables.sort(function(a, b){return b.table_recCount - a.table_recCount});
              break;
            case "8":
              tables.sort(function(a, b){return b.table_fieldCount - a.table_fieldCount});
              break;              
            default:
              tables.sort(function(a, b){return b.table_primaryKeyFieldsCount - a.table_primaryKeyFieldsCount});
          }

          $.each(tables, function(i, obj){
            //console.log(obj.name);
            // var dataContent = "<span class='label label-success'>" + obj.RecCount + "</span>";
            var tableType = "";
            if(obj.table_remarks == null){obj.table_remarks = ""};
            if(obj.table_type == "VIEW"){ tableType = '(' + obj.table_type + ')'};
            var option = '<option class="fontsize" value="' + obj.table_name + '" data-subtext="' + tableType + ' ' + obj.table_remarks +  ' ' + obj.table_stats + '">'
             + obj.table_name + '</option>';
            table.append(option);
            // $('#modPKTables').append(option);
            // table.append('<option class="fontsize" value=' + obj.name + '>' + obj.name + '</option>');
          });
          table.selectpicker('refresh');
          // $('#modPKTables').selectpicker('refresh');
          // localStorage.setItem('tables', JSON.stringify(tables));
        }
      }
      else {
        console.log("GetTables");
        $.ajax({
        type: 'POST',
        url: "GetTables",
        dataType: 'json',
        async: true,
        success: function(data) {
          console.log(data);
          $.each(data.TABLES, function(tableName, tableType){
            var option;
            if(tableType == "VIEW"){
              option = '<option class="fontsize" value="' + tableName + '" data-subtext="(' + tableType + ')">' + tableName + '</option>';
            }
            if(tableType == "TABLE"){
              option = '<option class="fontsize" value="' + tableName + '">' + tableName + '</option>';
            }
            if(tableType == ""){
              option = '<option class="fontsize" value="' + tableName + '" data-subtext="(OTHER)">' + tableName + '</option>';
              console.log(option);
            }
            table.append(option);
          });
          table.selectpicker('refresh');
        },
        error: function(data) {
            console.log(data);
        }

      });
    }
  }
  })
}

// function GetCognosLocales(){
//   $.when(
//     $.ajax({
//         type: 'POST',
//         url: "GetCognosLocales",
//         dataType: 'json',
//
//         success: function(data) {
//           cognosLocales = data.cognosLocales;
//           console.log(cognosLocales);
//           $.each(cognosLocales, function(i, locale){
//             var option = '<option class="fontsize" value="' + locale + '">' + locale + '</option>';
//             $("#languagesSelect").append(option);
//           });
//           $("#languagesSelect").selectpicker('refresh');
//         },
//         error: function(data) {
//             console.log(data);
//             showalert("GetCognosLocales()", "GetCognosLocales failed.", "alert-danger", "bottom");
//         }
//     })
//   )
//   .then(
//       GetCurrentProject()
//   );
// }

function ChooseField(table, id){
  table.empty();

  var datas = $datasTable.bootstrapTable('getData');

  console.log(datas);

  $.each(datas, function(i, obj){
    if(obj._id == id){
      $.each(obj.fields, function(j, field){
        var icon = "";
        if(field.pk){
          icon = "<i class='glyphicon glyphicon-star'></i>";
        }
        if(field.indexed && !field.pk){
          icon = "<i class='glyphicon glyphicon-star-empty'></i>";
        }
        var label = field.label;
        var subText = icon;
        if(label){
          subText += ' - ' + label;
        }

        table.append('<option class="fontsize" value="' + field.field_name + '" data-subtext="' + subText + '">' + field.field_name + '</option>');
      });
      table.selectpicker('refresh');
    }
  });

  var parms = {"table": id};
  console.log(parms);

  if( table.has('option').length == 0 ) {
    $.ajax({
        type: 'POST',
        url: "GetFields",
        dataType: 'json',
        data: JSON.stringify(parms),

        success: function(data) {
          console.log(data);
            $.each(data.DATAS, function(index, detail){
              var icon = "";
              if(detail.pk){
                icon = "<i class='glyphicon glyphicon-star'></i>";
              }
              if(detail.indexed && !detail.pk){
                icon = "<i class='glyphicon glyphicon-star-empty'></i>";
              }

              var label = detail.label;
              var subText = icon;
              if(label){
                subText += ' - ' + label;
              }

              table.append('<option class="fontsize" value"' + detail.field_name + '" data-subtext="' + subText + '">' + detail.field_name + '</option>');
            });
            table.selectpicker('refresh');
            // showalert("ChooseField()", "ChooseField was successfull.", "alert-success", "bottom");
        },
        error: function(data) {
            console.log(data);
            showalert("ChooseField()", "ChooseField failed.", "alert-danger", "bottom");
        }

    });

  }

}

function showalert(title, message, alertType, area, $el) {

    $('#alertmsg').remove();

    var timeout = 5000;

    if(area == undefined){
      area = "bottom";
    }
    if(alertType.match('warning')){
      area = "bottom";
      timeout = 10000;
    }
    if(alertType.match('danger')){
      area = "bottom";
      timeout = 30000;
    }

    var $newDiv;

    if(alertType.match('alert-success|alert-info')){
      $newDiv = $('<div/>')
       .attr( 'id', 'alertmsg' )
       .html(
          '<h4>' + title + '</h4>' +
          '<p>' +
          message +
          '</p>'
        )
       .addClass('alert ' + alertType + ' flyover flyover-' + area);
    }
    else{
      $newDiv = $('<div/>')
       .attr( 'id', 'alertmsg' )
       .html(
          '<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>' +
          '<h4>' + title + '</h4>' +
          '<p>' +
          '<strong>' + message + '</strong>' +
          '</p>'
        )
       .addClass('alert ' + alertType + ' alert-dismissible flyover flyover-' + area);
    }

    if($el){
      $el.append($newDiv);
    }
    else{
      $('#Alert').append($newDiv);
    }

    if ( !$('#alertmsg').is( '.in' ) ) {
      $('#alertmsg').addClass('in');

      setTimeout(function() {
         $('#alertmsg').removeClass('in');
      }, timeout);
    }
}

function TestDBConnection() {

    $.ajax({
        type: 'POST',
        url: "TestDBConnection",
        dataType: 'json',

        success: function(data) {
            console.log(data);
            showalert("TestDBConnection()", "Connection to database was successfull.", "alert-success", "bottom");
        },
        error: function(data) {
            console.log(data);
            showalert("TestDBConnection()", "Connection to database failed.", "alert-danger", "bottom");
        }

    });

}

function OpenSetProjectModal(){

 $projectFileModal.modal('toggle');
}

function SetProjectName(){
  var projectName = $projectFileModal.find('#filePath').val();
  console.log("projectName=" + projectName);
	if (!$.isNumeric(projectName)) {
	    showalert("SetProjectName()", "Enter a numeric value.", "alert-warning", "bottom");
	    return;
  	}
  $.ajax({
		type: 'POST',
		url: "SetProjectName",
		// dataType: 'json',
		data: "projectName=" + "model-" + projectName,

		success: function(data) {
			Publish();
		},
		error: function(data) {
			showalert("SetProjectName()", "Error when setting projectName.", "alert-danger", "bottom");
		}
	});

  $projectFileModal.modal('toggle');
}

function Test(){

  // forceLogout();

  // var form = $("#hiddenForm").html();
  // // var form = $(".bootbox-body #myForm").html();
  // bootbox.confirm({
  //     title: "Publish Model",
  //     message: form,
  //     callback: function (result) {
  //         // do something
  //         console.log($("#fn").val());
  //         var username = form.find('input[name=FirstName]').val();
  //         console.log(username);          
  //     }
  // });

}

function showAL(){
  $("#showActionLog").trigger("click");
}

function forceLogout(message){

  if(!message){
    message = "";
  }

  var text = 'Login again for changes to take effect.' + message;
  
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


function promptTeasing(){

  bootbox.confirm({
    title: "Feature is not available",
    message: "Some features are available only when project is connected to a database.<br>Ask for more informations ?",
    buttons: {
      cancel: {
          label: 'Cancel',
          className: 'btn btn-default'
      },
      confirm: {
          label: 'Ask',
          className: 'btn btn-primary'
      }
    },
    callback: function(result){
      if(result){
        console.log("ask was clicked...");
        window.open("http://data-accelerator.com/contact/");
      }
    }
  });                  


}

function promptPublish(){

  $datasTable.bootstrapTable("filterBy", {});
	var data = $datasTable.bootstrapTable('getData');

  if (data.length == 0) {
    showalert("Publish()", "Nothing to publish.", "alert-warning", "bottom");
    return;
  }

  var html = [
    '<header><h3>Publish model</h3>',
    '<hr />',
    '<div id="publishAlert"></div>',
    '</header><body>',
    '<form><div class="form-group">',
        '<label for="projectName">Name</label>',
        '<input type="text" class="form-control" id="projectName" placeholder="my model"></div>',
      '<div class="checkbox">',
        '<label><input type="checkbox" id="applyActionLogs">Apply Action Logs</label>',
      '</div>',
      '<button class="btn btn-default" type="button" onclick="showAL()">Show Action Logs</button>',
      '</form></body>'
  ].join('');

  bootbox.confirm(html,
    function(result){
    if(result){
      var projectName = $("#projectName").val();
      console.log(projectName);
      var applyActionLogs = $("#applyActionLogs").prop('checked');
      console.log(applyActionLogs);
      if(projectName.length == 0){
        ShowAlert("Give the model a name.", "alert-warning", $("#publishAlert"));              
        return false;
      }
      Publish(projectName, applyActionLogs);
    }

  });

}

function Publish(projectName, applyActionLogs){

  $datasTable.bootstrapTable("filterBy", {});
	var data = $datasTable.bootstrapTable('getData');

  if (data.length == 0) {
    showalert("Publish()", "Nothing to publish.", "alert-warning", "bottom");
    return;
  }

  var view = $('#ViewsTable').bootstrapTable("getData");

  var parms = {"projectName": projectName,
  "applyActionLogs": applyActionLogs,
  "data": JSON.stringify(data),
  "view": JSON.stringify(view)
  };

  $.ajax({
    type: 'POST',
    url: "SendQuerySubjects",
    dataType: 'json',
    data: JSON.stringify(parms),

    success: function(data) {
      // $('#DatasTable').bootstrapTable('load', data);
      console.log(data);
      if(data.STATUS == "OK"){
        showalert("Publish()", data.MESSAGE, "alert-success", "bottom");
        parms = {"publishedModelName": projectName};
        $.ajax({
          type: 'POST',
          url: "ZipPublishedModel",
          dataType: 'json',
          data: JSON.stringify(parms),
      
          success: function(data) {
            // $('#DatasTable').bootstrapTable('load', data);
            console.log(data);
            if(data.STATUS == "OK"){
              showalert(data.FROM, data.MESSAGE, "alert-success", "bottom");
              window.location.href = "DLPublishedModel";
              // window.location.href = "DLActionLogFile";
            }
          },
          error: function(data) {
            showalert("ERROR", "Uknown error occured.", "alert-danger", "bottom");
          }
        });
      
      }
      else{
        showalert(data.ERROR, data.MESSAGE + ": " + data.AXISFAULT + "<br>" + data.TROUBLESHOOTING, "alert-danger");
        window.location.href = "DLErrorFile";
      }
    },
    error: function(data) {
      showalert("Publish()", "Publish failed.", "alert-danger", "bottom");
    }
  });

}

function ViewsGeneratorFromMerge(){

  $datasTable.bootstrapTable("filterBy", {});
  var data = $datasTable.bootstrapTable('getData');

  var views = $('#ViewsTable').bootstrapTable("getData");
  
  var parms = {"qss": JSON.stringify(data), "views": JSON.stringify(views)};
  console.log(parms);

  $.ajax({
    type: 'POST',
    url: "ViewsGeneratorFromMerge",
    dataType: 'json',
    data: JSON.stringify(parms),

    success: function(data) {
      if(data.STATUS == "OK"){
        showalert(data.FROM, data.MESSAGE, "alert-success", "bottom");
        $('#ViewsTable').bootstrapTable("load", Object.values(data.DATAS));
      }
      else{
        showalert(data.ERROR, data.MESSAGE, "alert-danger");
      }
    },
    error: function(data) {
      showalert("ViewsGeneratorFromMerge()", "Generating failed.", "alert-danger", "bottom");
    }
  });  

}

function removeSpecialChars(name){
  // var re = new RegExp("[#@&~€$£ÀÁÂÃÄÅàáâãäåÒÓÔÕÖØòóôõöøÈÉÊËèéêëÇçÌÍÎÏìíîïÙÚÛÜùúûüÿÑñŒœ¨%µ\{\}\\[\\]\^\*/<>\?\!;§]", "gi");
  // return name.replace(re, "");
  return name;
}

function SaveModel(){

  if(activeTab == "View"){
    $qsTab.tab('show');
  }

  $datasTable.bootstrapTable("filterBy", {});
  var modelName;
  var qss = $datasTable.bootstrapTable('getData');
  var view = $('#ViewsTable').bootstrapTable("getData");

  if (qss.length == 0) {
    showalert("SaveModel()", "Nothing to save.", "alert-warning", "bottom");
    return;
  }

  // $.each(data, function(i, obj){
  //   obj.label = "";
  //   obj.description = "";
  // });

  bootbox.prompt({
    size: "small",
    title: "Enter model name",
    callback: function(result){
     /* result = String containing user input if OK clicked or null if Cancel clicked */
    modelName = result;
    if(!modelName){
      return;
    }
    var parms = {modelName: removeSpecialChars(modelName), 
      qss: JSON.stringify(qss),
      view: JSON.stringify(view)
    };
    console.log(parms);

   	$.ajax({
   		type: 'POST',
   		url: "SaveModel",
   		dataType: 'json',
   		data: JSON.stringify(parms),

   		success: function(data) {
        console.log(data);
        if(data.STATUS == "OK"){
   			    showalert("SaveModel()", "Model saved successfully as " + data.FILENAME + ".", "alert-success", "bottom");
        }
        else{
          showalert("Saving model " + modelName + " failed.", data.MESSAGE + "<br>" + data.TROUBLESHOOTING, "alert-danger", "bottom");
        }
   		},
   		error: function(data) {
   			showalert("SaveModel()", "Saving model failed.", "alert-danger", "bottom");
   		}
   	});

    // ApplyFilter();

    }
  });

}

function GetModelList(){

  $modelListModal.modal('toggle');

	$.ajax({
		type: 'POST',
		url: "GetModelList",
		dataType: 'json',

		success: function(data) {
      modelList = data;
      data.sort(function(a, b) {
        return b - a;
      });
      console.log("modelList");
      console.log(modelList);
			// showalert("GetModelList()", "Model list get successfull.", "alert-success", "bottom");

		},
		error: function(data) {
			showalert("GetModelList()", "Getting model list failed.", "alert-danger", "bottom");
		}
	});

}

function initGlobals(){

  var qss = $datasTable.bootstrapTable("getData");

  if(qss.length == 0){
    return;
  }

  var folderSet = new Set();
  var dimensionSet = new Set();

  $.each(qss,function(i, qs){
    if(qs.folder != ''){
      folderSet.add(qs.folder);
    }
    $.each(qs.fields, function(j, field){
      $.each(field.dimensions, function(k, dimension){
        if(!dimension.dimension.startsWith('[')){
          dimensionSet.add(dimension.dimension);
        }
      })
    })
  });

  var folders = getArrayFromSet(folderSet);
  var dimensions = getArrayFromSet(dimensionSet);
  var langs = Object.keys(qss[0].labels);

  $("#langSelect").empty();
  $.each(langs, function(i, lang){
    var dc = '<span class="lang-lg lang-lbl-full" lang="' + lang + '"></span>' ;
    var option = '<option class="fontsize" value="' + lang + '" data-content=\'' + dc + '\'></option>';
    $("#langSelect").append(option);
  })

  console.log(folders);

  $("#foldSelect").empty();
  $.each(folders, function(i, folder){
    var option = '<option class="fontsize" value="' + folder + '">' + folder + '</option>"';
    $("#foldSelect").append(option);
  })
  $("#foldSelect").selectpicker('refresh');

  console.log(dimensions);

  $("#dimSelect").empty();
  $.each(dimensions, function(i, dimension){
    var option = '<option class="fontsize" value="' + dimension + '">' + dimension + '</option>"';
    $("#dimSelect").append(option);
  })
  $("#dimSelect").selectpicker('refresh');

  console.log(qss);

}

function OpenModel(id){

  var modelName;

  $.each(modelList, function(i, obj){
    if(obj.id == id){
      modelName = obj.name;
    }
  });

	$.ajax({
		type: 'POST',
		url: "OpenModel",
		dataType: 'json',
    data: "model=" + modelName,

		success: function(data) {
      if(data.querySubjects){
        console.log(data.querySubjects);
        $datasTable.bootstrapTable("load", data.querySubjects);
      }
      else{
        console.log(data);
        $datasTable.bootstrapTable("load", data);
      }
      if(data.views){
        $("#viewTab").removeClass('disabled');
        $viewTab.prop('disabled',false);
        $("#ViewsTable").bootstrapTable("load", data.views);
      }
      $refTab.tab('show');
      initGlobals();
      $finTab.tab('show');
      $qsTab.tab('show');
      if(data.querySubjects){
        var langs = Object.keys(data.querySubjects[0].labels);
      }
      else{
        var langs = Object.keys(data[0].labels);
      }
      console.log(langs[0]);
      $("#langSelect").selectpicker('val', langs[0]);
      $("#langSelect").selectpicker('refresh');
      SetLanguage(langs[0]);

      $("#qsSelect").empty();
      $.each($datasTable.bootstrapTable("getData"), function(i, data){
        if(data.type.match("Final|Ref")){
          var option = '<option class="fontsize" value="' + data._id + '" data-subtext="' + data.type + '" data-content="">' + data._id + '</option>';
          $("#qsSelect").append(option);
        }
      })
      $("#qsSelect").selectpicker('refresh');


		},
		error: function(data) {
			showalert("OpenModel()", "Opening model failed.", "alert-danger", "bottom");
		}
	});

  $modelListModal.modal('toggle');

}

function GetDBDataType() {

	$.ajax({
        type: 'POST',
        url: "GetDataType",
        dataType: 'json',

        success: function(data) {
			       dbDataType = data;
             dbDataType.push({value: "", text: ""});
        },
        error: function(data) {
            console.log(data);
        }

    });
}


function Logout(){

  $('#modLogout').modal('toggle');

  return;

  bootbox.confirm({
    message: "Do you really want to logout ?",
    buttons: {
        confirm: {
            label: 'Yes',
            className: 'btn-danger'
        },
        cancel: {
            label: 'No',
            className: 'btn-success'
        }
    },
    callback: function (result) {

      if(!result){
        return;
      }

      // $.ajax({
      //       type: 'POST',
      //       url: "ibm_security_logout",
      //       data: 'logout=Logout&logoutExitPage=%2Flogin.html',
      //
      //       success: function(data) {
      //         console.log(data);
      //       },
      //       error: function(data) {
      //         console.log(data);
      //       }
      //
      //   });

        var XHR = new XMLHttpRequest();
          var FD  = new FormData();

          // Push our data into our FormData object
          FD.append("logout", "Logout");
          // FD.append("logoutExitPage", "login.html");

          // Define what happens on successful data submission
          XHR.addEventListener('load', function(event) {
            console.log('Yeah! Data sent and response loaded.');
          });

          // Define what happens in case of error
          XHR.addEventListener('error', function(event) {
            console.log('Oops! Something went wrong.');
          });

          // Set up our request
          XHR.open('POST', 'Logout');

          XHR.setRequestHeader("Content-type","application/x-www-form-urlencoded");

          // Send our FormData object; HTTP headers are set automatically
          XHR.send(FD);

    }

});
}

function Reset() {

	var success = "OK";

	$.ajax({
        type: 'POST',
        url: "Reset",
        dataType: 'json',

        success: function(data) {
			success = "OK";
        },
        error: function(data) {
            console.log(data);
   			success = "KO";
        }

    });

	if (success == "KO") {
		showalert("Reset()", "Operation failed.", "alert-danger", "bottom");
	}

  // window.location = window.location.href+'?eraseCache=true';
  // localStorage.setItem('dbmd', null);
	location.reload(true);

}

function GetTableData(){
		var data = $datasTable.bootstrapTable("getData");
		console.log("data=");
		console.log(JSON.stringify(data));
    console.log(data);

}

function GetDBMDFromCache(){

    $.when(
      $.ajax({
        type: 'POST',
        url: "GetDBMDFromCache",
        dataType: 'json',
        async: true,
        success: function(data) {
          // dbmd = data;
        }
      })
    )
    .then(
      function(data){
        dbmd = data;
        console.log("Got dbmd");
        // localStorage.setItem('dbmd', JSON.stringify(dbmd));
        console.log(dbmd);
        ChooseTable($tableList);
      }
    );

}

function GetCurrentProject(){
  $.ajax({
    type: 'POST',
    url: "GetCurrentProject",
    dataType: 'json',
    async: true,
    success: function(data) {
      currentProject = data.data;
      if(data.data){
        if(data.USER){
          $("#logout").text("Logout " + data.USER);
        }
        var lang = data.data.languages[0];
        var dc = '<span class="lang-lg lang-lbl-full" lang="' + lang + '"></span>' ;
  			var option = '<option class="fontsize" value="' + lang + '" data-content=\'' + dc + '\'></option>';
  			$("#langSelect").append(option);

        $("#langSelect").selectpicker('val', lang);
        $("#langSelect").selectpicker('refresh');

        if(data.data.resource.jndiName != "XML"){
          GetDBMDFromCache();
          GetDBDataType();
        }
        else{
          $.ajax({
            url: "UploadXML",
            type: "POST",
            success: function(data) {
              console.log(data);
                if(data.STATUS == "OK"){
        
                  var table = $('#tables');
        
                  table.empty();
        
                  $.each(data.TABLES, function(tableName, tableType){
                    if(tableType == "VIEW"){
                      var option = '<option class="fontsize" value="' + tableName + '" data-subtext="(' + tableType + ')">' + tableName + '</option>';
                    }
                    if(tableType == "TABLE"){
                      var option = '<option class="fontsize" value="' + tableName + '">' + tableName + '</option>';
                    }
                    table.append(option);
                  });
                  table.selectpicker('refresh');
                  GetDBMDFromCache();
                }
                if(data.MESSAGE == "model.xml not found."){
                  bootbox.confirm({
                    title: data.MESSAGE,
                    message: "Do you want to upload one ?",
                    buttons: {
                      cancel: {
                          label: 'Cancel',
                          className: 'btn btn-default'
                      },
                      confirm: {
                          label: 'Upload',
                          className: 'btn btn-primary'
                      }
                    },
                    callback: function(result){
                      if(result){
                        $('#XMLFile').trigger('click');
                      }
                    }
                  });                  
                }
            },
            error: function(data) {
              console.log(data);
            }
          });

          // $("#liSetHidden").addClass('disabled');
          // $("#setHidden").unbind('click');
          // $("#liRefreshTableDBMD").addClass('disabled');
          // $("#refreshTableDBMD").unbind('click');
          // $("#liAddSqlRel").addClass('disabled');
          // $("#addSqlRel").unbind('click');
          // $("#liSearchTool").addClass('disabled');
          // $("#searchTool").unbind('click');
          // $("#liSaveRel").addClass('disabled');
          // $("#saveRel").unbind('click');

        }

      }
      console.log(data.data);
    }
  })
}

function OpenQueryModal(){
  $('#queryModal').modal('toggle');
  // GetLabelsQueries();
}

function SaveQueries(){

  var backupName;

  var vide = true;

  if($("#tableLabel").val().trim().length > 1) {
    vide = false;
  }
  if($("#tableDescription").val().trim().length > 1) {
    vide = false;
  }
  if($("#columnLabel").val().trim().length > 1) {
    vide = false;
  }
  if($("#columnDescription").val().trim().length > 1) {
    vide = false;
  }

  console.log(vide);

  if(vide){
    ShowAlert("Nothing to save.", "alert-warning", $("#queryModalAlert"));
    return;
  }


  bootbox.prompt({
    size: "small",
    title: "Enter backup name",
    callback: function(result){
       /* result = String containing user input if OK clicked or null if Cancel clicked */
      backupName = result;
      if(!backupName){
        return;
      }

      var queries = {};
      queries.tlQuery = $('#tableLabel').val().replace(/[^\x20-\x7E]/gmi, "");
      queries.tdQuery = $('#tableDescription').val().replace(/[^\x20-\x7E]/gmi, "");
      queries.clQuery = $('#columnLabel').val().replace(/[^\x20-\x7E]/gmi, "");
      queries.cdQuery = $('#columnDescription').val().replace(/[^\x20-\x7E]/gmi, "");

      var parms = {backupName: backupName, data: queries};

     	$.ajax({
     		type: 'POST',
     		url: "SaveQueries",
     		dataType: 'json',
     		data: JSON.stringify(parms),

     		success: function(data) {
     			ShowAlert("Queries saved successfully.", "alert-success", $("#queryModalAlert"));
     		},
     		error: function(data) {
     			ShowAlert("Saving Queries failed.", "alert-danger", $("#queryModalAlert"));
     		}
     	});
    }
  });

}

function ShowAlert(message, alertType, $el) {

    $('#alertmsg').remove();

    var timeout = 3000;

    if(alertType.match('alert-warning')){
      timeout = 10000;
    }
    if(alertType.match('alert-danger')){
      timeout = 15000;
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

function GetQueriesList(){

	$.ajax({
		type: 'POST',
		url: "GetQueriesList",
		dataType: 'json',

		success: function(data) {
      queriesList = data;
      data.sort(function(a, b) {
        return b - a;
      });
			// ShowAlert("GetQueriesList()", "Queries list get successfull.", "alert-success", "bottom");
      if(queriesList.length > 0){
        $('#modQueriesList').modal('toggle');
      }
      else{
        ShowAlert("No queries list available on server.", "alert-info", $("#queryModalAlert"));
      }

		},
		error: function(data) {
			ShowAlert("Getting queries list failed.", "alert-danger", $("#queryModalAlert"));
		}
	});

}

function GetCsvLabelsMultiLang(){

  $('#csvLabelModal .collapse').each(function () {
      $(this).collapse('hide');
  });

  var aliasesSet = new Set();
  $datasTable.bootstrapTable("filterBy", {});
  $.each($datasTable.bootstrapTable('getData'), function(i, qs){
    console.log(qs)
    aliasesSet.add(qs.table_alias);
    $.each(qs.relations, function(j, relation){
      aliasesSet.add(relation.pktable_alias);
    })
  })

  var aliases = getArrayFromSet(aliasesSet);

  if(aliases.length == 0){
    ShowAlert("Import at least one Query Subject.", "alert-warning", $("#csvLabelModalAlert"));
    return;
  }

  var qss = {};

  $.each(aliases, function(i, alias){

    $.each($datasTable.bootstrapTable('getData'), function(i, qs){

      if(alias == qs.table_alias){
        qss[alias] = {};
        qss[alias].alias = alias;
        qss[alias].table = qs.table_name;
      }

      $.each(qs.relations, function(j, relation){
        aliasesSet.add(relation.pktable_alias);
      })
    })
      
  })

  

  var lang = $("#langSelect").find("option:selected").val();
  var parms = {};
  parms.aliases = qss;
  parms.lang = lang;
  console.log(parms);

  $.ajax({
    type: 'POST',
    url: "GetCsvLabels",
    dataType: 'json',
    data: JSON.stringify(parms),

    success: function(labels) {
      console.log(labels);
      if(labels.STATUS == "KO"){
        ShowAlert("ERROR: " + labels.MESSAGE + "<br>TROUBLESHOOTING: " + labels.TROUBLESHOOTING, "alert-danger", $("#csvLabelModalAlert"));
      }
      else{
        labels = labels.DATAS;
        var currentLanguage = $('#langSelect').find("option:selected").val();
        $.each($datasTable.bootstrapTable('getData'), function(i, qs){
          if(labels[qs.table_name]){
            if(!qs.labels[currentLanguage] || qs.labels[currentLanguage] == ""){
              if(labels[qs.table_name].table_remarks){
                qs.labels[currentLanguage] = labels[qs.table_name].table_remarks;
              }
              else{
                qs.labels[currentLanguage] = "";
              }
            }
            if(!qs.descriptions[currentLanguage] || qs.descriptions[currentLanguage] == ""){
              if(labels[qs.table_name].table_description){
                qs.descriptions[currentLanguage] = labels[qs.table_name].table_description;
              }
              else{
                qs.descriptions[currentLanguage] = "";
              }
            }
            if(!qs.label || qs.label == ""){
              qs.label = qs.labels[currentLanguage];
            }
            if(!qs.description || qs.description == ""){
              qs.description = qs.descriptions[currentLanguage];
            }
            $.each(qs.fields, function(j, field){
              if(labels[qs.table_name].columns[field.field_name]){
                if(!field.labels[currentLanguage] || field.labels[currentLanguage] == ""){
                  if(labels[qs.table_name].columns[field.field_name].column_remarks){
                    field.labels[currentLanguage] = labels[qs.table_name].columns[field.field_name].column_remarks;
                  }
                  else{
                    field.labels[currentLanguage] = "";
                  }
                }
                if(!field.descriptions[currentLanguage] || field.descriptions[currentLanguage] == ""){
                  if(labels[qs.table_name].columns[field.field_name].column_description){
                    field.descriptions[currentLanguage] = labels[qs.table_name].columns[field.field_name].column_description;
                  }
                  else{
                    field.descriptions[currentLanguage] = "";
                  }
                }
                if(!field.label || field.label == ""){
                  field.label = field.labels[currentLanguage];
                }
                if(!field.description || field.description == ""){
                  field.description = field.descriptions[currentLanguage];
                }
              }
            })
            $.each(qs.relations, function(j, relation){
              if(labels[relation.pktable_name]){
                if(!relation.labels[currentLanguage] || relation.labels[currentLanguage] == ""){
                  if(labels[relation.pktable_name].table_remarks){
                    relation.labels[currentLanguage] = labels[relation.pktable_name].table_remarks;
                  }
                  else{
                    relation.labels[currentLanguage] = "";
                  }
                }
                if(!relation.descriptions[currentLanguage] || relation.descriptions[currentLanguage] == ""){
                  if(labels[relation.pktable_name].table_description){
                    relation.descriptions[currentLanguage] = labels[relation.pktable_name].table_description;
                  }
                  else{
                    relation.descriptions[currentLanguage] = "";
                  }
                }
                if(!relation.label || relation.label == ""){
                  relation.label = relation.labels[currentLanguage];
                }
                if(!relation.description || relation.description == ""){
                  relation.description = relation.descriptions[currentLanguage];
                }
              }
            })
          }
          if(labels[qs.table_alias]){
            if(!qs.labels[currentLanguage] || qs.labels[currentLanguage] == ""){
              if(labels[qs.table_alias].table_remarks){
                qs.labels[currentLanguage] = labels[qs.table_alias].table_remarks;
              }
              else{
                qs.labels[currentLanguage] = "";
              }
            }
            if(!qs.descriptions[currentLanguage] || qs.descriptions[currentLanguage] == ""){
              if(labels[qs.table_alias].table_description){
                qs.descriptions[currentLanguage] = labels[qs.table_alias].table_description;
              }
              else{
                qs.descriptions[currentLanguage] = "";
              }
            }
            if(!qs.label || qs.label == ""){
              qs.label = qs.labels[currentLanguage];
            }
            if(!qs.description || qs.description == ""){
              qs.description = qs.descriptions[currentLanguage];
            }
            $.each(qs.fields, function(j, field){
              if(labels[qs.table_alias].columns[field.field_name]){
                if(!field.labels[currentLanguage] || field.labels[currentLanguage] == ""){
                  if(labels[qs.table_alias].columns[field.field_name].column_remarks){
                    field.labels[currentLanguage] = labels[qs.table_alias].columns[field.field_name].column_remarks;
                  }
                  else{
                    field.labels[currentLanguage] = "";
                  }
                }
                if(!field.descriptions[currentLanguage] || field.descriptions[currentLanguage] == ""){
                  if(labels[qs.table_alias].columns[field.field_name].column_description){
                    field.descriptions[currentLanguage] = labels[qs.table_alias].columns[field.field_name].column_description;
                  }
                  else{
                    field.descriptions[currentLanguage] = "";
                  }
                }
                if(!field.label || field.label == ""){
                  field.label = field.labels[currentLanguage];
                }
                if(!field.description || field.description == ""){
                  field.description = field.descriptions[currentLanguage];
                }
              }
            })
            $.each(qs.relations, function(j, relation){
              if(labels[relation.pktable_alias]){
                if(!relation.labels[currentLanguage] || relation.labels[currentLanguage] == ""){
                  if(labels[relation.pktable_alias].table_remarks){
                    relation.labels[currentLanguage] = labels[relation.pktable_alias].table_remarks;
                  }
                  else{
                    relation.labels[currentLanguage] = "";
                  }
                }
                if(!relation.descriptions[currentLanguage] || relation.descriptions[currentLanguage] == ""){
                  if(labels[relation.pktable_alias].table_description){
                    relation.descriptions[currentLanguage] = labels[relation.pktable_alias].table_description;
                  }
                  else{
                    relation.descriptions[currentLanguage] = "";
                  }
                }
                if(!relation.label || relation.label == ""){
                  relation.label = relation.labels[currentLanguage];
                }
                if(!relation.description || relation.description == ""){
                  relation.description = relation.descriptions[currentLanguage];
                }
              }
            })
          }

        })
        $('#csvLabelModal').modal('toggle');
        $refTab.tab('show');
        $qsTab.tab('show');
      }

    },
    error: function(data) {
      console.log(data);
    }
  });



}

function GetLabelsMultiLang(){

  var tablesSet = new Set();
  $datasTable.bootstrapTable("filterBy", {});
  $.each($datasTable.bootstrapTable('getData'), function(i, qs){
    console.log(qs)
    tablesSet.add(qs.table_name);
    $.each(qs.relations, function(j, relation){
      tablesSet.add(relation.pktable_name);
    })
  })

  var tables = getArrayFromSet(tablesSet);

  if(tables.length == 0){
    ShowAlert("Import at least one Query Subject.", "alert-warning", $("#queryModalAlert"));
    return;
  }

  // $('#queryModal').modal('toggle');

  var lang = $("#langSelect").find("option:selected").val();
  var parms = {};
  parms.tables = tables;
  parms.lang = lang;
  parms.tlQuery = $('#tableLabel').val().replace(/[^\x20-\x7E]/gmi, "");
  parms.tdQuery = $('#tableDescription').val().replace(/[^\x20-\x7E]/gmi, "");
  parms.clQuery = $('#columnLabel').val().replace(/[^\x20-\x7E]/gmi, "");
  parms.cdQuery = $('#columnDescription').val().replace(/[^\x20-\x7E]/gmi, "");

  console.log(JSON.stringify(parms));

  $.ajax({
    type: 'POST',
    url: "GetLabels",
    dataType: 'json',
    data: JSON.stringify(parms),

    success: function(labels) {
      console.log(labels);
      if(labels.STATUS == "KO"){
        ShowAlert("ERROR: " + labels.MESSAGE + "<br>TROUBLESHOOTING: " + labels.TROUBLESHOOTING, "alert-danger", $("#queryModalAlert"));
      }
      else{
        labels = labels.DATAS;
        var currentLanguage = $('#langSelect').find("option:selected").val();
        $.each($datasTable.bootstrapTable('getData'), function(i, qs){
          if(labels[qs.table_name]){
            if(!qs.labels[currentLanguage] || qs.labels[currentLanguage] == ""){
              if(labels[qs.table_name].table_remarks){
                qs.labels[currentLanguage] = labels[qs.table_name].table_remarks;
              }
              else{
                qs.labels[currentLanguage] = "";
              }
            }
            if(!qs.descriptions[currentLanguage] || qs.descriptions[currentLanguage] == ""){
              if(labels[qs.table_name].table_description){
                qs.descriptions[currentLanguage] = labels[qs.table_name].table_description;
              }
              else{
                qs.descriptions[currentLanguage] = "";
              }
            }
            if(!qs.label || qs.label == ""){
              qs.label = qs.labels[currentLanguage];
            }
            if(!qs.description || qs.description == ""){
              qs.description = qs.descriptions[currentLanguage];
            }
            $.each(qs.fields, function(j, field){
              if(labels[qs.table_name].columns[field.field_name]){
                if(!field.labels[currentLanguage] || field.labels[currentLanguage] == ""){
                  if(labels[qs.table_name].columns[field.field_name].column_remarks){
                    field.labels[currentLanguage] = labels[qs.table_name].columns[field.field_name].column_remarks;
                  }
                  else{
                    field.labels[currentLanguage] = "";
                  }
                }
                if(!field.descriptions[currentLanguage] || field.descriptions[currentLanguage] == ""){
                  if(labels[qs.table_name].columns[field.field_name].column_description){
                    field.descriptions[currentLanguage] = labels[qs.table_name].columns[field.field_name].column_description;
                  }
                  else{
                    field.descriptions[currentLanguage] = "";
                  }
                }
                if(!field.label || field.label == ""){
                  field.label = field.labels[currentLanguage];
                }
                if(!field.description || field.description == ""){
                  field.description = field.descriptions[currentLanguage];
                }
              }
            })
            $.each(qs.relations, function(j, relation){
              if(labels[relation.pktable_name]){
                if(!relation.labels[currentLanguage] || relation.labels[currentLanguage] == ""){
                  if(labels[relation.pktable_name].table_remarks){
                    relation.labels[currentLanguage] = labels[relation.pktable_name].table_remarks;
                  }
                  else{
                    relation.labels[currentLanguage] = "";
                  }
                }
                if(!relation.descriptions[currentLanguage] || relation.descriptions[currentLanguage] == ""){
                  if(labels[relation.pktable_name].table_description){
                    relation.descriptions[currentLanguage] = labels[relation.pktable_name].table_description;
                  }
                  else{
                    relation.descriptions[currentLanguage] = "";
                  }
                }
                if(!relation.label || relation.label == ""){
                  relation.label = relation.labels[currentLanguage];
                }
                if(!relation.description || relation.description == ""){
                  relation.description = relation.descriptions[currentLanguage];
                }
              }
            })
          }
        })
        // $('#queryModal').modal('toggle');
        $refTab.tab('show');
        $qsTab.tab('show');
      }
    },
    error: function(data) {
      console.log(data);
    }
  });


}

$('#modQueriesList').on('shown.bs.modal', function() {
  $(this).find('.modal-body').empty();
  var list = '<div class="container-fluid"><div class="row"><form role="form"><div class="form-group">';
  list += '<input id="searchinput" class="form-control" type="search" placeholder="Search..." /></div>';
  list += '<div id="searchlist" class="list-group">';

  $.each(queriesList, function(index, object){
    list += '<a href="#" class="list-group-item" onClick="OpenQueries(' + object.id + '); return false;"><span>' + object.name + '</span></a>';
  });
  list += '</div></form><script>$("#searchlist").btsListFilter("#searchinput", {itemChild: "span", initial: false, casesensitive: false});</script>';
  $(this).find('.modal-body').append(list);
});

function OpenQueries(id){

  var queriesName;

  $.each(queriesList, function(i, obj){
    if(obj.id == id){
      queriesName = obj.name;
    }
  });

  console.log("queriesName=" + queriesName);

	$.ajax({
		type: 'POST',
		url: "OpenQueries",
		dataType: 'json',
    data: "queries=" + queriesName,

		success: function(queries) {
      console.log(queries);
      $('#tableLabel').val(queries.tlQuery);
      $('#tableDescription').val(queries.tdQuery);
      $('#columnLabel').val(queries.clQuery);
      $('#columnDescription').val(queries.cdQuery);
      // showalert("OpenQueries()", "Queries opened successfully.", "alert-success", "bottom");

		},
		error: function(data) {
			showalert("OpenQueries()", "Opening queries failed.", "alert-danger", "bottom");
		}
	});

  $('#modQueriesList').modal('toggle');

}

$("#ulXMLModel").click(function(){
  console.log("ulXMLModel was clicked");
  $('#ulXMLModelFile').trigger('click');

})

$('#ulXMLModelFile').change(function(){
  var file = $(this)[0].files[0];
  console.log(file);

  var fd = new FormData();
  fd.append('file', file, 'model.xml');
  console.log(fd);

  $.ajax({
    url: "UploadXML",
    type: "POST",
    data: fd,
    enctype: 'multipart/form-data',
    // dataType: 'application/text',
    processData: false,  // tell jQuery not to process the data
    contentType: false,   // tell jQuery not to set contentType
    success: function(data) {
      console.log(data);
        if(data.STATUS == "OK"){

          var table = $('#tables');

          table.empty();

          $.each(data.TABLES, function(tableName, tableType){
            var option;
            if(tableType == "VIEW"){
              option = '<option class="fontsize" value="' + tableName + '" data-subtext="(' + tableType + ')">' + tableName + '</option>';
            }
            if(tableType == "TABLE"){
              option = '<option class="fontsize" value="' + tableName + '">' + tableName + '</option>';
            }
            if(tableType == ""){
              option = '<option class="fontsize" value="' + tableName + '" data-subtext="(OTHER)">' + tableName + '</option>';
              console.log(option);
            }
            table.append(option);
          });
          table.selectpicker('refresh');
          showalert(data.FROM, data.MESSAGE, "alert-success", "bottom");

        }
        else{
          showalert(data.FROM, data.MESSAGE, "alert-danger", "bottom");
        }
    },
		error: function(data) {
      console.log(data);
		}
  });  

  // $.ajax({
  //   url: "UploadXMLModel",
  //   type: "POST",
  //   data: fd,
  //   enctype: 'multipart/form-data',
  //   // dataType: 'application/text',
  //   processData: false,  // tell jQuery not to process the data
  //   contentType: false,   // tell jQuery not to set contentType
  //   success: function(data) {
  //     console.log(data);
  //       if(data.STATUS == "OK"){
  //         showalert(data.FROM, data.MESSAGE, "alert-success", "bottom");
  //       }
  //       else{
  //         showalert(data.FROM, data.MESSAGE, "alert-danger", "bottom");
  //       }
	// 	},
	// 	error: function(data) {
  //     console.log(data);
	// 	}
  // });

  $(this).val('');  

})

saveLabels.addEventListener('click', function(event){

  var qss = $datasTable.bootstrapTable("getData");

  if(qss.length == 0){
    showalert("Nothing to do", "No Query Subjects found.", "alert-info", "bottom");
    return;
  }

  var lang = $("#langSelect").find("option:selected").val();

  var parms = {"qss": JSON.stringify(qss), "delim": ";", "lang": lang}

  console.log(parms);

  $.ajax({
    type: 'POST',
    url: "SaveQssLabels",
    dataType: 'json',
    data: JSON.stringify(parms),

    success: function(data) {
      console.log(data);
      if(data.STATUS == "OK"){
        showalert(data.FROM, data.MESSAGE, "alert-success", "bottom");
        window.location.href = "DLLabels";
      }
      else{
        showalert(data.FROM, data.MESSAGE, "alert-warning", "bottom");
      }
    },
    error: function(data) {
      console.log(data);
    }
  });
  
  event.preventDefault();
}, false);

$('#XMLFile').change(function(){
  var file = $(this)[0].files[0];
  console.log(file);

  var fd = new FormData();
  fd.append('file', file, 'model.xml');
  console.log(fd);

  $.ajax({
    url: "UploadXML",
    type: "POST",
    data: fd,
    enctype: 'multipart/form-data',
    // dataType: 'application/text',
    processData: false,  // tell jQuery not to process the data
    contentType: false,   // tell jQuery not to set contentType
    success: function(data) {
      console.log(data);
        if(data.STATUS == "OK"){

          var table = $('#tables');

          table.empty();

          $.each(data.TABLES, function(tableName, tableType){
            var option;
            if(tableType == "VIEW"){
              option = '<option class="fontsize" value="' + tableName + '" data-subtext="(' + tableType + ')">' + tableName + '</option>';
            }
            if(tableType == "TABLE"){
              option = '<option class="fontsize" value="' + tableName + '">' + tableName + '</option>';
            }
            if(tableType == ""){
              option = '<option class="fontsize" value="' + tableName + '" data-subtext="(OTHER)">' + tableName + '</option>';
              console.log(option);
            }
            table.append(option);
          });
          table.selectpicker('refresh');
          showalert(data.FROM, data.MESSAGE, "alert-success", "bottom");


        }
		},
		error: function(data) {
      console.log(data);
		}
  });

  $(this).val('');  

})

$("#logout").click(function(){
  Logout();
})


$("#testDBConnection").click(function(){
  if(currentProject){
    console.log(currentProject.resource.jndiName);
    if(currentProject.resource.jndiName == "XML"){
      promptTeasing();  
    }
    else{
      TestDBConnection();
    }
  }
})

$("#searchTool").click(function(){
  Search();
})

$("#loadFromXML").click(function(){
  console.log("loadFromXML was clicked");
  $('#XMLFile').trigger('click');

})

$("#dlActionLog").click(function(){
  $.ajax({
		type: 'POST',
		url: "ZipActionLog",
		dataType: 'json',
		success: function(data) {
      console.log(data);
      if(data.STATUS == "OK"){
        showalert(data.FROM, data.MESSAGE, "alert-success");
        window.location.href = "DownloadActionLog";

      }
      else{
        showalert(data.FROM, data.ERROR, "alert-warning");
      }
		},
		error: function(data) {
      console.log(data);
      showalert("ERROR", "Unknown", "alert-danger");
		}
	});
  
})

$("#rmActionLog").click(function(){

  $.ajax({
		type: 'POST',
		url: "RemoveActionLog",
		dataType: 'json',
		success: function(data) {
      console.log(data);
      if(data.STATUS == "OK"){
        showalert(data.FROM, data.MESSAGE, "alert-success");
      }
      else{
        showalert(data.FROM, data.ERROR, "alert-danger");
      }
		},
		error: function(data) {
      console.log(data);
      showalert("ERROR", "Unknown", "alert-danger");
		}
	});
  
})

$("#showActionLog").click(function(){
  GetActionLogList()
})

function GetActionLogList(){

  $.ajax({
		type: 'POST',
		url: "GetActionLogList",
		dataType: 'json',
		success: function(data) {
      console.log(data);
      if(data.DATAS){
        var list = '<ul class="list-group">';
        $.each(Object(data.DATAS), function(key, value){
          list += '<li class="list-group-item">' + key + '</li>';
        })
        list += '</ul>';

        bootbox.alert({
          title: "Following Action Log(s) are already uploaded :",
          message: list,
          callback: function(result){
          }
        });  


      }
      else{
        showalert(data.FROM, data.INFO, "alert-info");
      }
		},
		error: function(data) {
      console.log(data);
		}
	});

}

$("#actionLogFile").change(function(){
  var fd = new FormData();

  var file = $(this)[0].files[0];
  // console.log(file);
  // var fileName = file.name;

  fd.append('file', file, file.name);
  // console.log(fd);

  $.ajax({
    url: "UploadActionLog",
    type: "POST",
    data: fd,
    enctype: 'multipart/form-data',
    // dataType: 'application/text',
    processData: false,  // tell jQuery not to process the data
    contentType: false,   // tell jQuery not to set contentType
    success: function(data) {
      console.log(data);
      if(data.STATUS == "OK"){
        showalert(data.FROM, data.FILENAME + " uploaded successfully.", "alert-success", "bottom");
      }
      else{
        showalert(data.FROM, data.ERROR + "<br>" + data.TROUBLESHOOTING, "alert-warning");
      }

		},
		error: function(data) {
      console.log(data);
      showalert("ERROR", "Unknown", "alert-danger");
		}
  });

  $(this).val('');  

})


$('#CSVViewsFile').change(function(){

  var fd = new FormData();

  var lang = $("#langSelect").find("option:selected").val();
  var parms = {};
  parms.lang = lang;
  console.log(parms);

  fd.append('json', JSON.stringify(parms));

  var file = $(this)[0].files[0];
  console.log(file);

  fd.append('file', file, 'views.csv');
  console.log(fd);

  $.ajax({
    url: "LoadViews",
    type: "POST",
    data: fd,
    enctype: 'multipart/form-data',
    // dataType: 'application/text',
    processData: false,  // tell jQuery not to process the data
    contentType: false,   // tell jQuery not to set contentType
    success: function(data) {
      console.log(data);
      if(data.STATUS == "OK"){
        showalert(data.FROM, data.MESSAGE, "alert-success", "bottom");
        // console.log(Object.values(data.DATAS));
        $('#ViewsTable').bootstrapTable("load", Object.values(data.DATAS));
        $("#viewTab").removeClass('disabled');
        $viewTab.prop('disabled',false);
      
      }
      else{
        showalert(data.ERROR, data.MESSAGE, "alert-danger");
      }

		},
		error: function(data) {
      console.log(data);
		}
  });

  $(this).val('');  

})

$('#CSVQssFile').change(function(){

  var fd = new FormData();

  var qss = $datasTable.bootstrapTable("getData");
  var lang = $("#langSelect").find("option:selected").val();
  var parms = {};
  parms.qss = JSON.stringify(qss);
  parms.lang = lang;
  console.log(parms);
  console.log(JSON.stringify(parms));

  fd.append('json', JSON.stringify(parms));

  var file = $(this)[0].files[0];
  console.log(file);

  fd.append('file', file, 'qss.csv');
  console.log(fd);

  $.ajax({
    url: "LoadQss",
    type: "POST",
    data: fd,
    enctype: 'multipart/form-data',
    // dataType: 'application/text',
    processData: false,  // tell jQuery not to process the data
    contentType: false,   // tell jQuery not to set contentType
    success: function(data) {
      console.log(data);
      if(data.STATUS == "OK"){
        showalert(data.FROM, data.MESSAGE, "alert-success", "bottom");
        // console.log(Object.values(data.DATAS));
        $('#DatasTable').bootstrapTable("load", Object.values(data.DATAS));
        // $("#viewTab").removeClass('disabled');
        // $viewTab.prop('disabled',false);
        $("#sortQss").trigger("click");
      
      }
      else{
        showalert(data.ERROR, data.MESSAGE, "alert-danger");
      }

		},
		error: function(data) {
      console.log(data);
		}
  });

  $(this).val('');  

})

$("#loadViews").click(function(){
  $('#CSVViewsFile').trigger('click');
})

$("#loadQss").click(function(){
  $('#CSVQssFile').trigger('click');
})

$("#ulActionLog").click(function(){
  $('#actionLogFile').trigger('click');

})


$("#updateModel").click(function(){

  var langs = $.map($('#langSelect option'), function(el) { 
    if($(el).val().length > 0){
      return $(el).val();
    }
  });

  var model = $datasTable.bootstrapTable('getData');

  var parms = {"model": JSON.stringify(model), "langs": JSON.stringify(langs)};  

  $.ajax({
    type: 'POST',
    url: "UpdateModel",
    dataType: 'json',
    data: JSON.stringify(parms),

    success: function(data) {
        console.log(data);

        if(jQuery.isEmptyObject(data.ADDED) && jQuery.isEmptyObject(data.REMOVED)  && jQuery.isEmptyObject(data.UPDATED)){
          bootbox.alert({
            message: "Model is already up to date.",
            size: "small",
            callback: function(result){
            }
          });          

        }
        else{

          $datasTable.bootstrapTable("load", data.MODEL);
          var list = '<ul class="list-group">';
          $.each(Object(data.ADDED), function(key, value){
            if(value.length > 0){
              list += '<li class="list-group-item">' + key + '<span class="badge badge-primary">+ ' + value.length + '</span></li>';
            }
          })
          $.each(Object(data.REMOVED), function(key, value){
            if(value.length > 0){
              list += '<li class="list-group-item">' + key + '<span class="badge badge-warning">- ' + value.length + '</span></li>';
            }
          })
          list += '</ul>';

          bootbox.alert({
            title: "Following Query Subjects have been updated successfuly :",
            message: list,
            callback: function(result){
            }
          });  
        }        

    },
    error: function(data) {
        console.log(data);
    }

  });

})

$('#setUnHiddenAll').click(function(){

  var qss = $datasTable.bootstrapTable('getData');
  if(qss.length > 0){

    $.each(qss, function(i, qs){
      $.each(qs.fields, function(j, field){
          field.hidden = false;
      })

    })
    $refTab.tab('show');
    $qsTab.tab('show');

  }
  else{
    showalert("No Query Subject found.", "alert-warning", "bottom");
    // $("#qsSelect").selectpicker("toggle");
  }
})


$('#setHiddenINLAll').click(function(){

  var lang = $("#langSelect").find("option:selected").val();
  console.log(lang);
  var qss = $datasTable.bootstrapTable('getData');
  if(qss.length > 0){

    $.each(qss, function(i, qs){
      $.each(qs.fields, function(j, field){
        if(!field.labels[lang]){
          field.hidden = true;
        }
        else{
          field.hidden = false;
        }
      })

    })
    $refTab.tab('show');
    $qsTab.tab('show');

  }
  else{qs
    showalert("No Query Subject found.", "alert-warning", "bottom");
    // $("#qsSelect").selectpicker("toggle");
  }
})

$('#setUnHidden').click(function(){

  var table = $("#qsSelect").find("option:selected").val();
  if(!table == ""){

    var qsId = $("#qsSelect").find("option:selected").text();
    var qss = $datasTable.bootstrapTable('getData');
    var qs;
    var index;
    $.each(qss, function(i, o){
      if(o._id.match(qsId)){
        qs = o;
        index = i;
        console.log(qs);
        $.each(qs.fields, function(j, field){
            field.hidden = false;
        })
      }
    })
    $refTab.tab('show');
    $qsTab.tab('show');
    $datasTable.bootstrapTable('expandRow', index);

  }
  else{
    showalert("No Query Subject selected.", "Select a Query Subject first.", "alert-warning", "bottom");
    $("#qsSelect").selectpicker("toggle");
  }
})


$('#setHiddenINL').click(function(){

    var table = $("#qsSelect").find("option:selected").val();
    var lang = $("#langSelect").find("option:selected").val();
    console.log(table);
    console.log(lang);
    if(!table == ""){
  
      var qsId = $("#qsSelect").find("option:selected").text();
      var qss = $datasTable.bootstrapTable('getData');
      var qs;
      var index;
      $.each(qss, function(i, o){
        if(o._id.match(qsId)){
          qs = o;
          index = i;
          console.log(qs);
          $.each(qs.fields, function(j, field){
            if(!field.labels[lang]){
              field.hidden = true;
            }
          })
        }
      })
      $refTab.tab('show');
      $qsTab.tab('show');
      $datasTable.bootstrapTable('expandRow', index);
  
    }
    else{
      showalert("No Query Subject selected.", "Select a Query Subject first.", "alert-warning", "bottom");
      $("#qsSelect").selectpicker("toggle");
    }
})

function saveHiddenQuery(){

  var qsId = $("#qsSelect").val();
  var query = $("#hiddenQuery").val().toUpperCase();
  var qss = $datasTable.bootstrapTable('getData');

  $.each(qss, function(i, o){
    if(o._id.match(qsId)){
      o.hiddenQuery = query;
      ShowAlert("hiddenQuery saved.", "alert-success", $("#hiddenQueryModalAlert"));
      return false;
    }
  })

}

$("#setHidden").click(function(){

  if(currentProject){
    console.log(currentProject.resource.jndiName);
    if(currentProject.resource.jndiName == "XML"){
      promptTeasing();  
    }
    else{
      // var qsId = $("#qsSelect").find("option:selected").text();
      var qsId = $("#qsSelect").val();
      console.log(qsId);
      if(qsId){
        $("#hiddenQueryModal").modal("toggle");
        $("#hiddenQueryModalLabel").text("SQL queries for hidden" + " - " + qsId);
        var qsId = $("#qsSelect").find("option:selected").text();
        var qss = $datasTable.bootstrapTable('getData');
        var qs;
        $.each(qss, function(i, o){
          if(o._id.match(qsId)){
            qs = o;
            return false;
          }
        })
      
        $("#hiddenQuery").val(qs.hiddenQuery);

      }
      else{
        showalert("No Query Subject selected.", "Select a Query Subject first.", "alert-warning", "bottom");
        $("#qsSelect").selectpicker("toggle");
      }

      console.log(currentProject);
    }
  }  
})

function setHidden(){

  var qsId = $("#qsSelect").find("option:selected").text();
  var qss = $datasTable.bootstrapTable('getData');
  var qs;
  var index;
  $.each(qss, function(i, o){
    if(o._id.match(qsId)){
      qs = o;
      index = i;
    }
  })

  var query = $("#hiddenQuery").val().toUpperCase();

  var parms = {"qs": JSON.stringify(qs), "query": query};

  qs.hiddenQuery = query;

  $.ajax({
		type: 'POST',
		url: "SetHidden",
		dataType: 'json',
    data: JSON.stringify(parms),

		success: function(data) {
      console.log(data.DATAS);
      qss[index] = data.DATAS;
      $refTab.tab('show');
      $qsTab.tab('show');
      $datasTable.bootstrapTable('expandRow', index);

		},
		error: function(data) {
      console.log(data);
		}
	});

  // $("#hiddenQueryModal").modal("toggle");

}

$("#setHiddenAll").click(function(){

  if(currentProject.resource.jndiName == "XML"){
    promptTeasing();
    return;  
  }  

  var qss = $datasTable.bootstrapTable('getData');
  console.log(qss);
  var parms = {"qss": JSON.stringify(qss)};
  console.log(parms);

  $.ajax({
		type: 'POST',
		url: "SetHiddenAll",
		dataType: 'json',
    data: JSON.stringify(parms),

		success: function(data) {
      console.log(data.DATAS);
      if(data.STATUS = "OK"){
        $datasTable.bootstrapTable('load', data.DATAS);
        $datasTable.bootstrapTable('collapseAllRows');
        showalert(data.FROM, data.MESSAGE, "alert-success", "bottom");
      }
		},
		error: function(data) {
      console.log(data);
		}
	});
})

$("#addLangMenu").click(function(){
	$('#langModal').modal('toggle');
	$("#langList").selectpicker("val", emptyOption);
	$("#langList").selectpicker("refresh");
})

$("#removeLabel").click(function(){

  var qs = $("#qsSelect").find("option:selected").val();

  if(qs == ""){
    showalert("", "Select a Query Subject.", "alert-warning", "bottom");
    $("#qsSelect").selectpicker('toggle');
    return;
  }

  var qsId = $("#qsSelect").find("option:selected").text();  
  console.log(qs);
  console.log(qsId);

  var lang = $("#langSelect").find("option:selected").val();
	var flag = '<span class="lang-sm lang-lbl-full" lang="' + lang + '"></span>';
  console.log(flag);

  bootbox.confirm({
    title: "Removing label.",
    message: flag + " label for " + qsId + " will be dropped.",
    buttons: {
      cancel: {
          label: 'Cancel',
          className: 'btn btn-default'
      },
      confirm: {
          label: 'Confirm',
          className: 'btn btn-primary'
      }
    },
    callback: function(result){
      if(result){
        removeLabel(lang, qsId);
				// $("#langSelect").find("option:selected").remove();
				// var emptyOption = '<option class="fontsize" value="" data-subtext="" data-content=""></option>';
				// $("#langSelect").selectpicker("val", currentProject.languages[0]);
				// $("#langSelect").selectpicker("refresh");
        // SetLanguage(currentProject.languages[0]);
      }
    }
  });

})


$("#removeLabels").click(function(){

  console.log("enter in removeLabels");

  var lang = $("#langSelect").find("option:selected").val();

	var flag = '<span class="lang-sm lang-lbl-full" lang="' + lang + '"></span>';

  bootbox.confirm({
    title: "Removing labels.",
    message: "All " + flag + " labels will be dropped.",
    buttons: {
      cancel: {
          label: 'Cancel',
          className: 'btn btn-default'
      },
      confirm: {
          label: 'Confirm',
          className: 'btn btn-primary'
      }
    },
    callback: function(result){
      if(result){
        removeLabels(lang);
				// $("#langSelect").find("option:selected").remove();
				// var emptyOption = '<option class="fontsize" value="" data-subtext="" data-content=""></option>';
				// $("#langSelect").selectpicker("val", currentProject.languages[0]);
				// $("#langSelect").selectpicker("refresh");
        // SetLanguage(currentProject.languages[0]);
      }
    }
  });

})

$("#removeLangMenu").click(function(){


	var lang = $("#langSelect").find("option:selected").val();

	var flag = '<span class="lang-sm lang-lbl-full" lang="' + lang + '"></span>';

  if(lang.match(currentProject.languages[0])){
    showalert("", "Language " + flag + " can't be removed.", "alert-warning", "bottom");
    return;
  }

	bootbox.confirm({
    title: "Removing language.",
    message: flag + " will be dropped.",
    buttons: {
      cancel: {
          label: 'Cancel',
          className: 'btn btn-default'
      },
      confirm: {
          label: 'Confirm',
          className: 'btn btn-primary'
      }
    },
    callback: function(result){
      if(result){
        removeLabels(lang);
				$("#langSelect").find("option:selected").remove();
				var emptyOption = '<option class="fontsize" value="" data-subtext="" data-content=""></option>';
				$("#langSelect").selectpicker("val", currentProject.languages[0]);
				$("#langSelect").selectpicker("refresh");
        SetLanguage(currentProject.languages[0]);
      }
    }
  });
})

function removeLabel(lang, qsId){
  console.log(lang);
  console.log(qsId);
  $datasTable.bootstrapTable("filterBy", {});
  var qss = $datasTable.bootstrapTable('getData');
  $.each(qss, function(i, qs){
    if(qs._id == qsId){
      console.log(qs);
      delete qs.labels[lang];
      delete qs.descriptions[lang];
      qs.label = "";
      qs.description = "";
      $.each(qs.fields, function(j, field){
        if(field.custom == false){
          delete field.labels[lang];
          delete field.descriptions[lang];
          field.label = "";
          field.description = "";
        }
      })
      $.each(qs.relations, function(j, rel){
        delete rel.labels[lang];
        delete rel.descriptions[lang];
        rel.label = "";
        rel.description = "";
      })
    }
  })
  $refTab.tab('show');
  $qsTab.tab('show');
}

function removeLabels(lang){
  console.log(lang);
  $datasTable.bootstrapTable("filterBy", {});
  var qss = $datasTable.bootstrapTable('getData');
  $.each(qss, function(i, qs){
    console.log(qs);
    delete qs.labels[lang];
    delete qs.descriptions[lang];
    qs.label = "";
    qs.description = "";
    $.each(qs.fields, function(j, field){
      if(field.custom == false){
        delete field.labels[lang];
        delete field.descriptions[lang];
        field.label = "";
        field.description = "";
      }
    })
    $.each(qs.relations, function(j, rel){
      delete rel.labels[lang];
      delete rel.descriptions[lang];
      rel.label = "";
      rel.description = "";
    })
  })
  console.log(qss);
  $refTab.tab('show');
  $qsTab.tab('show');
}

function removeFolder(fold){
  console.log(fold);
  $datasTable.bootstrapTable("filterBy", {});
  var qss = $datasTable.bootstrapTable('getData');
  $.each(qss, function(i, qs){
    if(qs.folder == fold){
      qs.folder = "";
    }
  })
  if(activeTab == "Query Subject"){
    $refTab.tab('show');
    $qsTab.tab('show');
  }
  console.log(qss);
}

function removeDimension(dim){
  console.log(dim);
  $datasTable.bootstrapTable("filterBy", {});
  var qss = $datasTable.bootstrapTable('getData');
  $.each(qss, function(i, qs){
    $.each(qs.fields, function(j, field){
      $.each(field.dimensions, function(k, dimension){
        if(dimension.dimension == dim){
          field.dimensions.splice(k, 1);
        }
      })
    })
    $.each(qs.relations, function(k, relation){
      if(relation.usedForDimensions == dim){
        relation.usedForDimensions = "";
      }
    })
  })
  if(activeTab == "Query Subject"){
    $refTab.tab('show');
    $qsTab.tab('show');
  }
  if(activeTab == "Reference"){
    $qsTab.tab('show');
    $refTab.tab('show');
  }
  console.log(qss);
}

function initLangList(){
	$.each(countryCodes, function(i, code){
		var dc = '<span class="lang-lg lang-lbl-full" lang="' + code + '"></span> - (' + code + ')' ;
		var option = '<option class="fontsize" value="' + code + '" data-content=\'' + dc + '\'></option>';
		$("#langList").append(option);
	});
	$("#langList").selectpicker("val", emptyOption);
	$("#langList").selectpicker("refresh");
}

$("#addLang").click(function(){
	var lang = ($("#langList").val());
	if(lang){
		if($('#langSelect option[value="' + lang + '"]').length == 0){
			var dc = '<span class="lang-lg lang-lbl-full" lang="' + lang + '"></span>' ;
			var option = '<option class="fontsize" value="' + lang + '" data-content=\'' + dc + '\'></option>';
			$("#langSelect").append(option);
			$("#langSelect").selectpicker("val", lang);
			$("#langSelect").selectpicker("refresh");
      var currentLanguage = $('#langSelect').find("option:selected").val();
      SetLanguage(currentLanguage);
		}
		$('#langModal').modal('toggle');
	}
})

var prevVal;
$("#langSelect").on('shown.bs.select', function(e) {
        prevVal = $(this).val();
});

$("#langSelect").on('changed.bs.select', function (e, clickedIndex, isSelected, previousValue) {

  console.log(e);

  console.log(clickedIndex);
  console.log(isSelected);

  console.log(previousValue);
  console.log($("#langSelect").find("option:selected").val());

  console.log("changed");

  var curLang = $("#langSelect").find("option:selected").val();

  var prevFlag = '<span class="lang-sm lang-lbl-full" lang="' + prevVal + '"></span>';

  var curFlag = '<span class="lang-sm lang-lbl-full" lang="' + curLang + '"></span>';

  bootbox.confirm({
    message: "Do you really want to switch from " + prevFlag + " to " + curFlag + " ?",
    buttons: {
        confirm: {
            label: 'Yes',
            className: 'btn-primary'
        },
        cancel: {
            label: 'No',
            className: 'btn-default'
        }
    },
    callback: function (result) {
      if(!result){
        $("#langSelect").selectpicker('val', prevVal);
        $("#langSelect").selectpicker('refresh');
        return;
      }
      var currentLanguage = $('#langSelect').find("option:selected").val();
      SetLanguage(currentLanguage);
    }

  });



});

$("#SQLLabels").click(function(){
  OpenQueryModal();
})

$("#addTableAlias").click(function(){
	var $id = $(this).attr("id");
  console.log("$id=" + $id);
	bootbox.prompt({
    size: "small",
    title: "Enter alias name",
    value: $("#alias").val(),
    callback: function(result){
       /* result = String containing user input if OK clicked or null if Cancel clicked */
      if(result != null){
        console.log(result);
        console.log($("#alias").val(result));
        console.log($("#alias").val());
      }
    }
  });
})

$("#sortTables").click(function(){
  if(currentProject){
    console.log(currentProject.resource.jndiName);
    // if(currentProject.resource.jndiName == "XML"){
    //   promptTeasing();  
    // }
    // else{
      SortOnStats();
    // }
  }
})

$("#refreshTableDBMD").click(function(){
  if(currentProject){
    console.log(currentProject.resource.jndiName);
    // if(currentProject.resource.jndiName == "XML"){
    //   promptTeasing();  
    // }
    // else{
      ChooseTable($tableList);
    // }
  }
})

$("#expandQS").click(function(){
  $("#DatasTable").bootstrapTable('expandAllRows');
})

$("#collapseQS").click(function(){
  $("#DatasTable").bootstrapTable('collapseAllRows');
})

$("#removeViews").click(function(){
  if($("#ViewsTable").bootstrapTable("getData").length == 0){
    return;
  }

  bootbox.confirm({
    title: "Removing Views.",
    message: "All Views will be dropped.",
    buttons: {
      cancel: {
          label: 'Cancel',
          className: 'btn btn-default'
      },
      confirm: {
          label: 'Confirm',
          className: 'btn btn-warning'
      }
    },
    callback: function(result){
      if(result){
        $("#ViewsTable").bootstrapTable("removeAll");
        // $("#qsSelect").empty();
        // $("#qsSelect").selectpicker('refresh');
      }
    }
  });

})


$("#removeQS").click(function(){
  if($("#DatasTable").bootstrapTable("getData").length == 0){
    return;
  }

  bootbox.confirm({
    title: "Removing Query Subjects.",
    message: "All Query Subjects will be dropped.",
    buttons: {
      cancel: {
          label: 'Cancel',
          className: 'btn btn-default'
      },
      confirm: {
          label: 'Confirm',
          className: 'btn btn-warning'
      }
    },
    callback: function(result){
      if(result){
        $datasTable.bootstrapTable("removeAll");
        $("#qsSelect").empty();
        $("#qsSelect").selectpicker('refresh');
      }
    }
  });

})

$("#generateViews").click(function(){
  $("#viewTab").removeClass('disabled');
  $viewTab.prop('disabled',false);
  ViewsGeneratorFromMerge();
})

$('#foldSelect').on('changed.bs.select', function (e, clickedIndex, isSelected, previousValue) {

});

$("#createView").click(function(){
	var $id = $(this).attr("id");
  console.log("$id=" + $id);
	bootbox.prompt({
    size: "small",
    title: "Enter view name",
    callback: function(result){
       /* result = String containing user input if OK clicked or null if Cancel clicked */
      if(result != null){
          // showalert("", 'View ' + result + ' successfully added.', "alert-success", "bottom");
          viewName = result;
          var parms = {viewName: viewName};
          $.ajax({
            type: 'POST',
            url: "GetEmptyView",
            dataType: 'json',
            data: JSON.stringify(parms),
   
            success: function(data) {
              console.log(data);
              showalert("", "View " + viewName + " created successfully.", "alert-success", "bottom");
              $("#viewTab").removeClass('disabled');
              $viewTab.prop('disabled',false);
              var index = $('#ViewsTable').bootstrapTable("getData").length;
              $('#ViewsTable').bootstrapTable('insertRow', {index: index, row: data.DATAS});
              console.log($('#ViewsTable').bootstrapTable("getData"));
              $("#viewTab_click").click();
            
            },
            error: function(data) {
              showalert("", "Creating view " + viewName + " failed.", "alert-danger", "bottom");
            }
          });          
      }
      else{
        showalert("", 'View ' + result + ' already exists.', "alert-info", "bottom");
      }
      }
    })
});


$("#addFold").click(function(){
	var $id = $(this).attr("id");
  console.log("$id=" + $id);
	bootbox.prompt({
    size: "small",
    title: "Enter folder name",
    callback: function(result){
       /* result = String containing user input if OK clicked or null if Cancel clicked */
      if(result != null){
				if($('#foldSelect option[value="' + result + '"]').length == 0){
					var option = '<option class="fontsize" value="' + result + '" data-subtext="" data-content="">' + result + '</option>';
					$("#foldSelect").append(option);
					$("#foldSelect").selectpicker("val", emptyOption);
					$("#foldSelect").selectpicker("refresh");
          showalert("", 'Folder ' + result + ' successfully added.', "alert-success", "bottom");
				}
        else{
          showalert("", 'Folder ' + result + ' already exists.', "alert-info", "bottom");
        }
      }
    }
  });

})

$("#addDim").click(function(){
	var $id = $(this).attr("id");
  console.log("$id=" + $id);
	bootbox.prompt({
    size: "small",
    title: "Enter dimension name",
    callback: function(result){
       /* result = String containing user input if OK clicked or null if Cancel clicked */
      if(result != null){
				if($('#dimSelect option[value="' + result + '"]').length == 0){
					var option = '<option class="fontsize" value="' + result + '" data-subtext="" data-content="">' + result + '</option>';
					$("#dimSelect").append(option);
					$("#dimSelect").selectpicker("val", emptyOption);
					$("#dimSelect").selectpicker("refresh");
          showalert("", 'Dimension ' + result + ' successfully added.', "alert-success", "bottom");
				}
        else{
          showalert("", 'Dimension ' + result + ' already exists.', "alert-info", "bottom");
        }
      }
    }
  });
})

$("#addHier").click(function(){
	var $id = $(this).attr("id");
  console.log("$id=" + $id);
	bootbox.prompt({
    size: "small",
    title: "Enter hierarchy name",
    callback: function(result){
       /* result = String containing user input if OK clicked or null if Cancel clicked */
      if(result != null){
				if($('#hierSelect option[value="' + result + '"]').length == 0){
					var option = '<option class="fontsize" value="' + result + '" data-subtext="" data-content="">' + result + '</option>';
					$("#hierSelect").append(option);
					$("#hierSelect").selectpicker("val", result);
					$("#hierSelect").selectpicker("refresh");
				}
      }
    }
  });
})

$("#labelFold").click(function(){
	var $id = $(this).attr("id");
  console.log("$id=" + $id);

	var fold = $("#foldSelect").find("option:selected").val();

	var lang = $("#langSelect").find("option:selected").val();
	var flag = '<span class="lang-sm lang-lbl-full" lang="' + lang + '"></span>';

	if(fold && lang){

		bootbox.prompt({
	    size: "large",
	    title: 'Enter folder ' + flag + ' label for ' + fold + '.',
	    callback: function(result){
	       /* result = String containing user input if OK clicked or null if Cancel clicked */
	      if(result != null){
					if($('#foldSelect option[value="' + result + '"]').length == 0){
						$("#foldSelect").find("option:selected").remove();
						var option = '<option class="fontsize" value="' + result + '" data-subtext="" data-content="">' + result + '</option>';
						$("#foldSelect").append(option);
						$("#foldSelect").selectpicker("val", result);
						$("#foldSelect").selectpicker("refresh");
					}
	      }
	    }
	  });
	}
})

$("#labelDim").click(function(){
	var $id = $(this).attr("id");
  console.log("$id=" + $id);

	var dim = $("#dimSelect").find("option:selected").val();

	var lang = $("#langSelect").find("option:selected").val();
	var flag = '<span class="lang-sm lang-lbl-full" lang="' + lang + '"></span>';

	if(dim && lang){

		bootbox.prompt({
	    size: "large",
	    title: 'Enter dimension ' + flag + ' label for ' + dim + '.',
	    callback: function(result){
	       /* result = String containing user input if OK clicked or null if Cancel clicked */
	      if(result != null){
					if($('#dimSelect option[value="' + result + '"]').length == 0){
						$("#dimSelect").find("option:selected").remove();
						var option = '<option class="fontsize" value="' + result + '" data-subtext="" data-content="">' + result + '</option>';
						$("#dimSelect").append(option);
						$("#dimSelect").selectpicker("val", result);
						$("#dimSelect").selectpicker("refresh");
					}
	      }
	    }
	  });
	}
})

$("#labelHier").click(function(){
	var $id = $(this).attr("id");
  console.log("$id=" + $id);

	var hier = $("#hierSelect").find("option:selected").val();

	var lang = $("#langSelect").find("option:selected").val();
	var flag = '<span class="lang-sm lang-lbl-full" lang="' + lang + '"></span>';

	if(hier && lang){

		bootbox.prompt({
	    size: "large",
	    title: 'Enter folder ' + flag + ' label for ' + hier + '.',
	    callback: function(result){
	       /* result = String containing user input if OK clicked or null if Cancel clicked */
	      if(result != null){
					if($('#hierSelect option[value="' + result + '"]').length == 0){
						$("#hierSelect").find("option:selected").remove();
						var option = '<option class="fontsize" value="' + result + '" data-subtext="" data-content="">' + result + '</option>';
						$("#hierSelect").append(option);
						$("#hierSelect").selectpicker("val", result);
						$("#hierSelect").selectpicker("refresh");
					}
	      }
	    }
	  });
	}
})

$("#removeFold").click(function(){
	var $id = $(this).attr("id");
  console.log("$id=" + $id);
	var fold = $("#foldSelect").find("option:selected").val();

  console.log(fold);
  console.log(currentProject.languages[0]);

  if(fold == undefined | fold == ""){
    showalert("", 'No valid folder selected.', "alert-warning", "bottom");
    return;
  }

	bootbox.confirm({
    title: "Removing folder.",
    message: fold + " will be dropped.",
    buttons: {
      cancel: {
          label: 'Cancel',
          className: 'btn btn-default'
      },
      confirm: {
          label: 'Confirm',
          className: 'btn btn-primary'
      }
    },
    callback: function(result){
      if(result){
        removeFolder(fold);
				$("#foldSelect").find("option:selected").remove();
				var emptyOption = '<option class="fontsize" value="" data-subtext="" data-content=""></option>';
				$("#foldSelect").selectpicker("val", emptyOption);
				$("#foldSelect").selectpicker("refresh");
      }
    }
  });
})

$("#removeDim").click(function(){
	var $id = $(this).attr("id");
  console.log("$id=" + $id);
	var dim = $("#dimSelect").find("option:selected").val();

  if(dim == undefined | dim == ""){
    showalert("", 'No valid dimension selected.', "alert-warning", "bottom");
    return;
  }

	bootbox.confirm({
    title: "Removing dimension.",
    message: dim + " will be dropped.",
    buttons: {
      cancel: {
          label: 'Cancel',
          className: 'btn btn-default'
      },
      confirm: {
          label: 'Confirm',
          className: 'btn btn-primary'
      }
    },
    callback: function(result){
      if(result){
        removeDimension(dim);
				$("#dimSelect").find("option:selected").remove();
				var emptyOption = '<option class="fontsize" value="" data-subtext="" data-content=""></option>';
				$("#dimSelect").selectpicker("val", emptyOption);
				$("#dimSelect").selectpicker("refresh");
      }
    }
  });
})

$("#removeHier").click(function(){
	var $id = $(this).attr("id");
  console.log("$id=" + $id);
	var hier = $("#hierSelect").find("option:selected").val();

	bootbox.confirm({
    title: "Removing hierarchy name.",
    message: hier + " will be dropped.",
    buttons: {
      cancel: {
          label: 'Cancel',
          className: 'btn btn-default'
      },
      confirm: {
          label: 'Confirm',
          className: 'btn btn-primary'
      }
    },
    callback: function(result){
      if(result){
				$("#hierSelect").find("option:selected").remove();
				var emptyOption = '<option class="fontsize" value="" data-subtext="" data-content=""></option>';
				$("#hierSelect").selectpicker("val", emptyOption);
				$("#hierSelect").selectpicker("refresh");
      }
    }
  });
})

$('#queryModal').on('shown.bs.modal', function() {
});

$('#queryModal').on('hidden.bs.modal', function() {
    $('#queryModal .collapse').each(function () {
        $(this).collapse('hide');
    });
});

// START

$("#addSqlLabel").click(function(){
  if(currentProject){
    console.log(currentProject.resource.jndiName);
    if(currentProject.resource.jndiName == "XML"){
      promptTeasing();  
    }
    else{
      $('#queryModal').modal('toggle');
    }
  }  
})

$("#addCsvLabel").click(function(){
  $('#csvLabelModal').modal('toggle');
})

$("#addCsvRel").click(function(){
  $("#csvRelationModal").modal('toggle');
})

$("#addCsvRelExp").click(function(){
  $("#csvRelationExpModal").modal('toggle');
})

$('#csvRelationModal').on('hidden.bs.modal', function() {
  $('#csvRelationModal .collapse').each(function () {
      $(this).collapse('hide');
  });
})

$('#csvRelationModal').on('shown.bs.modal', function() {
  $.ajax({
 		type: 'POST',
 		url: "GetCSVFirstRecords",
 		dataType: 'json',

 		success: function(data) {
      console.log(data);

      var $table = $("#csvRelationTable");
      $table.find("tr:gt(0)").remove();
      if(data.DATAS){
        if(data.DATAS.relation){
          $.each(data.DATAS.relation, function(i, record){
            $table.append($('<tr>')
            .append($('<td>').append(record.FK_NAME))
            .append($('<td>').append(record.PK_NAME))
            .append($('<td>').append(record.FKTABLE_NAME))
            .append($('<td>').append(record.PKTABLE_NAME))
            .append($('<td>').append(record.KEY_SEQ))
            .append($('<td>').append(record.FKCOLUMN_NAME))
            .append($('<td>').append(record.PKCOLUMN_NAME))
            )
          })
          $table.append($('<tr>')
            .append($('<td>').append("..."))
            .append($('<td>').append("..."))
            .append($('<td>').append("..."))
            .append($('<td>').append("..."))
            .append($('<td>').append("..."))
            .append($('<td>').append("..."))
            .append($('<td>').append("..."))
          )
          $('#delCsvRelation').prop('disabled', false);
        }
        else{
          $table.append($('<tr>')
            .append($('<td style="text-align: center; vertical-align: middle;" colspan="7">').append("no record found"))
          )
          $('#delCsvRelation').prop('disabled', true);
        }
      }
 		},
 		error: function(data) {
      console.log(data);
 		}
 	});

})

$('#csvLabelModal').on('hidden.bs.modal', function() {
    $('#searchSelect').selectpicker('deselectAll');
    $('#csvLabelModal .collapse').each(function () {
        $(this).collapse('hide');
    });

});


$('#csvLabelModal').on('shown.bs.modal', function() {

  var currentLanguage = $('#langSelect').find("option:selected").val();
  var parms = {"lang": currentLanguage};

  $.ajax({
 		type: 'POST',
 		url: "GetCSVFirstRecords",
 		dataType: 'json',
    data: JSON.stringify(parms),

 		success: function(data) {
      console.log(data);

      var $table = $("#csvTableLabelTable");
      $table.find("tr:gt(0)").remove();
      if(data.DATAS.tableLabel){
        $.each(data.DATAS.tableLabel, function(i, record){
          $table.append($('<tr>')
            .append($('<td>').append(record.tableName))
            .append($('<td>').append(record.tableLabel))
          )
        })
        $table.append($('<tr>')
          .append($('<td>').append("..."))
          .append($('<td>').append("..."))
        )
        $('#delCsvTableLabel').prop('disabled', false);
      }
      else{
        $table.append($('<tr>')
          .append($('<td style="text-align: center; vertical-align: middle;" colspan="2">').append("no record found"))
        )
        $('#delCsvTableLabel').prop('disabled', true);
      }

      $table = $("#csvTableDescriptionTable");
      $table.find("tr:gt(0)").remove();
      if(data.DATAS.tableDescription){
        $.each(data.DATAS.tableDescription, function(i, record){
          $table.append($('<tr>')
            .append($('<td>').append(record.tableName))
            .append($('<td>').append(record.tableDescription))
          )
        })
        $table.append($('<tr>')
          .append($('<td>').append("..."))
          .append($('<td>').append("..."))
        )
        $('#delCsvTableDescription').prop('disabled', false);
      }
      else{
        $table.append($('<tr>')
          .append($('<td style="text-align: center; vertical-align: middle;" colspan="2">').append("no record found"))
        )
        $('#delCsvTableDescription').prop('disabled', true);
      }

      $table = $("#csvColumnLabelTable");
      $table.find("tr:gt(0)").remove();
      if(data.DATAS.columnLabel){
        $.each(data.DATAS.columnLabel, function(i, record){
          $table.append($('<tr>')
            .append($('<td>').append(record.tableName))
            .append($('<td>').append(record.columnName))
            .append($('<td>').append(record.columnLabel))
          )
        })
        $table.append($('<tr>')
          .append($('<td>').append("..."))
          .append($('<td>').append("..."))
          .append($('<td>').append("..."))
        )
        $('#delCsvColumnLabel').prop('disabled', false);
      }
      else{
        $table.append($('<tr>')
          .append($('<td style="text-align: center; vertical-align: middle;" colspan="3">').append("no record found"))
        )
        $('#delCsvColumnLabel').prop('disabled', true);
      }

      $table = $("#csvColumnDescriptionTable");
      $table.find("tr:gt(0)").remove();
      if(data.DATAS.columnDescription){
        $.each(data.DATAS.columnDescription, function(i, record){
          $table.append($('<tr>')
            .append($('<td>').append(record.tableName))
            .append($('<td>').append(record.columnName))
            .append($('<td>').append(record.columnDescription))
          )
        })
        $table.append($('<tr>')
          .append($('<td>').append("..."))
          .append($('<td>').append("..."))
          .append($('<td>').append("..."))
        )
        $('#delCsvColumnDescription').prop('disabled', false);
      }
      else{
        $table.append($('<tr>')
          .append($('<td style="text-align: center; vertical-align: middle;" colspan="3">').append("no record found"))
        )
        $('#delCsvColumnDescription').prop('disabled', true);
      }

 		},
 		error: function(data) {
      console.log(data);
 		}
 	});

});

$("#csvTableLabelFile").change(function(){
  var currentLanguage = $('#langSelect').find("option:selected").val();
  UploadCSV($(this), 'tableLabel-' + currentLanguage + '.csv', $("#csvTableLabelTable"));
});

$("#csvTableDescriptionFile").change(function(){
  var currentLanguage = $('#langSelect').find("option:selected").val();
  UploadCSV($(this), 'tableDescription-' + currentLanguage + '.csv', $("#csvTableDescriptionTable"));
});

$("#csvColumnLabelFile").change(function(){
  var currentLanguage = $('#langSelect').find("option:selected").val();
  UploadCSV($(this), 'columnLabel-' + currentLanguage + '.csv', $("#csvColumnLabelTable"));
});

$("#csvColumnDescriptionFile").change(function(){
  var currentLanguage = $('#langSelect').find("option:selected").val();
  UploadCSV($(this), 'columnDescription-' + currentLanguage + '.csv', $("#csvColumnDescriptionTable"));
});

$("#csvRelationFile").change(function(){
  UploadCSV($(this), 'relation.csv', $("#csvRelationTable"));
});

$("#csvRelationExpFile").change(function(){
  UploadCSV($(this), 'relationExp.csv', $("#csvRelationExpTable"));
});

delCsvTableLabel.addEventListener('click', function(event){
  var $table = $("#csvTableLabelTable");
  $table.find("tr:gt(0)").remove();
  $table.append($('<tr>')
    .append($('<td style="text-align: center; vertical-align: middle;" colspan="7">').append("no record found"))
  )
  event.preventDefault();
}, false);

delCsvTableDescription.addEventListener('click', function(event){
  var $table = $("#csvTableDescriptionTable");
  $table.find("tr:gt(0)").remove();
  $table.append($('<tr>')
    .append($('<td style="text-align: center; vertical-align: middle;" colspan="7">').append("no record found"))
  )
  event.preventDefault();
}, false);

delCsvColumnLabel.addEventListener('click', function(event){
  var $table = $("#csvColumnLabelTable");
  $table.find("tr:gt(0)").remove();
  $table.append($('<tr>')
    .append($('<td style="text-align: center; vertical-align: middle;" colspan="7">').append("no record found"))
  )
  event.preventDefault();
}, false);

delCsvColumnDescription.addEventListener('click', function(event){
  var $table = $("#csvColumnDescriptionTable");
  $table.find("tr:gt(0)").remove();
  $table.append($('<tr>')
    .append($('<td style="text-align: center; vertical-align: middle;" colspan="7">').append("no record found"))
  )
  event.preventDefault();
}, false);

delCsvRelation.addEventListener('click', function(event){
  var $table = $("#csvRelationTable");
  $table.find("tr:gt(0)").remove();
  $table.append($('<tr>')
    .append($('<td style="text-align: center; vertical-align: middle;" colspan="7">').append("no record found"))
  )
  event.preventDefault();
}, false);

$('#delCsvTableLabel').click(function(){
  var currentLanguage = $('#langSelect').find("option:selected").val();
  deleteCsv(['tableLabel-' + currentLanguage + '.csv']);
})

$('#delCsvTableDescription').click(function(){
  var currentLanguage = $('#langSelect').find("option:selected").val();
  deleteCsv(['tableDescription-' + currentLanguage + '.csv']);
})

$('#delCsvColumnLabel').click(function(){
  var currentLanguage = $('#langSelect').find("option:selected").val();
  deleteCsv(['columnLabel-' + currentLanguage + '.csv']);
})

$('#delCsvTableDescription').click(function(){
  var currentLanguage = $('#langSelect').find("option:selected").val();
  deleteCsv(['columnDescription-' + currentLanguage + '.csv']);
})

$('#delCsvRelation').click(function(){
  deleteCsv(['relation.csv']);
})

function deleteCsv(files){

  var parms = {"files": files};
  console.log(parms);

  $.ajax({
 		type: 'POST',
 		url: "DeleteCSV",
 		dataType: 'json',
    data: JSON.stringify(parms),

 		success: function(data) {
      console.log(data)
      if(data.STATUS == 'OK'){
        var removed = "";
        if(data.REMOVED){
          removed = data.REMOVED.join("<br>");
          switch(parms.files[0]){
            case 'tableLabel.csv':
              $('#delCsvTableLabel').prop('disabled', true);
              break;
            case 'tableDescription.csv':
              $('#delCsvTableDescription').prop('disabled', true);
              break;
            case 'columnLabel.csv':
              $('#delCsvColumnLabel').prop('disabled', true);
              break;
            case 'columnDescription.csv':
              $('#delCsvColumnDescription').prop('disabled', true);
              break;
            case 'relation.csv':
              $('#delCsvRelation').prop('disabled', true);
              break;
          }
        }
        var alert = 'alert-info';
        var msg = "No change to save.";
        if(removed.trim().length > 1){
          alert = 'alert-success'
          msg = "Changed saved successfully.<br>Following file(s) removed:<br>" + removed;
        }
        switch(parms.files[0]){
          case 'relation.csv':
            ShowAlert(msg, alert, $("#csvRelationModalAlert"));
            break;
          default:
            ShowAlert(msg, alert, $("#csvLabelModalAlert"));
        }
      }
      else{
        switch(parms.files[0]){
          case 'relation.csv':
            ShowAlert(msg, alert, $("#csvRelationModalAlert"));
            ShowAlert(data.MESSAGE, "alert-danger", $("#csvRelationModalAlert"));
            break;
          default:
            ShowAlert(data.MESSAGE, "alert-danger", $("#csvLabelModalAlert"));
        }
      }

 		},
 		error: function(data) {
      console.log(data)
 		}
 	});
}

function UploadCSV($el, fileName, $table){

  var file = $el[0].files[0];
  console.log(file);

  var fd = new FormData();
  fd.append('file', file, fileName);
  console.log(fileName);

  $.ajax({
    url: "UploadCSV",
    type: "POST",
    data: fd,
    enctype: 'multipart/form-data',
    // dataType: 'application/text',
    processData: false,  // tell jQuery not to process the data
    contentType: false,   // tell jQuery not to set contentType
    success: function(data) {
      console.log(data);
        if(data.STATUS == "OK"){
          switch(fileName){
            case 'relation.csv':
              ShowAlert(data.MESSAGE, "alert-success", $("#csvRelationModalAlert"));
              break;
            case 'relationExp.csv':
              ShowAlert(data.MESSAGE, "alert-success", $("#csvRelationExpModalAlert"));
              break;
            default:
              ShowAlert(data.MESSAGE, "alert-success", $("#csvLabelModalAlert"));
          }
          $table.find("tr:gt(0)").remove();

          $.each(data.DATAS, function(i, record){
            switch(fileName){
                case (fileName.match(/^tableLabel/) || {}).input:
                  $table.append($('<tr>')
                    .append($('<td>').append(record.tableName))
                    .append($('<td>').append(record.tableLabel))
                  )
                  $('#delCsvTableLabel').prop('disabled', false);
                  break;
                case (fileName.match(/^tableDescription/) || {}).input:
                  $table.append($('<tr>')
                    .append($('<td>').append(record.tableName))
                    .append($('<td>').append(record.tableDescription))
                  )
                  $('#delCsvTableDescription').prop('disabled', false);
                  break;
                case (fileName.match(/^columnLabel/) || {}).input:
                  $table.append($('<tr>')
                    .append($('<td>').append(record.tableName))
                    .append($('<td>').append(record.columnName))
                    .append($('<td>').append(record.columnLabel))
                  )
                  $('#delCsvColumnLabel').prop('disabled', false);
                  break;
                case (fileName.match(/^columnDescription/) || {}).input:
                  $table.append($('<tr>')
                  .append($('<td>').append(record.tableName))
                  .append($('<td>').append(record.columnName))
                  .append($('<td>').append(record.columnDescription))
                  )
                  $('#delCsvColumnDescription').prop('disabled', false);
                  break;
                case 'relation.csv':
                  $table.append($('<tr>')
                  .append($('<td>').append(record.FK_NAME))
                  .append($('<td>').append(record.PK_NAME))
                  .append($('<td>').append(record.FKTABLE_NAME))
                  .append($('<td>').append(record.PKTABLE_NAME))
                  .append($('<td>').append(record.KEY_SEQ))
                  .append($('<td>').append(record.FKCOLUMN_NAME))
                  .append($('<td>').append(record.PKCOLUMN_NAME))
                  )
                  $('#delCsvRelation').prop('disabled', false);
                  break;
                case 'relationExp.csv':
                  $table.append($('<tr>')
                  .append($('<td>').append(record.JOIN_NAME))
                  .append($('<td>').append(record.FKTABLE_NAME))
                  .append($('<td>').append(record.PKTABLE_NAME))
                  .append($('<td>').append(record.RELATION_EXPRESSION))
                  )
                  $('#delCsvRelationExp').prop('disabled', false);
                  break;
            }
          })
          switch(fileName){

            case (fileName.match(/^tableLabel/) || {}).input:
            case (fileName.match(/^tableDescription/) || {}).input:
              $table.append($('<tr>')
                .append($('<td>').append("..."))
                .append($('<td>').append("..."))
              )
              break;
            case (fileName.match(/^columnLabel/) || {}).input:
            case (fileName.match(/^columnDescription/) || {}).input:
              $table.append($('<tr>')
                .append($('<td>').append("..."))
                .append($('<td>').append("..."))
                .append($('<td>').append("..."))
              )
              break;
            case 'relation.csv':
              $table.append($('<tr>')
                .append($('<td>').append("..."))
                .append($('<td>').append("..."))
                .append($('<td>').append("..."))
                .append($('<td>').append("..."))
                .append($('<td>').append("..."))
                .append($('<td>').append("..."))
                .append($('<td>').append("..."))
              )
              break;
          }
        }
        else{
          switch(fileName){
            case 'relation.csv':
              ShowAlert(data.MESSAGE + "<br>" + data.TROUBLESHOOTING, "alert-danger", $("#csvRelationModalAlert"));
              break;
            default:
              ShowAlert(data.MESSAGE + "<br>" + data.TROUBLESHOOTING, "alert-danger", $("#csvLabelModalAlert"));
          }
          $table.find("tr:gt(0)").remove();
          $table.append($('<tr>')
            .append($('<td style="text-align: center; vertical-align: middle;" colspan="7">').append("no record found"))
          )

        }
		},
		error: function(data) {
      console.log(data);
		}
  });

  $el.val('');

}


$("#addSqlRel").click(function(){
  if(currentProject){
    console.log(currentProject.resource.jndiName);
    if(currentProject.resource.jndiName == "XML"){
      promptTeasing();  
    }
    else{
      $('#relsQueryModal').modal('toggle');
    }
  }
})

$("#ulWks").click(function(){

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
              $("#dlWks").trigger('click');             
            }
        }
    }
  })

})

$("#rmPrj").click(function(){

  var message = [
    '<span style="font-size: 25px" class="glyphicon glyphicon-warning-sign" aria-hidden="true"></span>',
    '&nbsp;&nbsp;&nbsp;&nbsp; All your data will be dropped.'
  ].join("");


  bootbox.dialog({ 
    title: 'Remove current project',
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
        remove: {
            label: 'Remove current project',
            className: 'btn-warning',
            callback: function(){
              $.ajax({
                type: 'POST',
                url: "RMPrj",
                dataType: 'json',
            
                success: function(data) {
                  console.log(data);
                  showalert(data.FROM, data.MESSAGE, "alert-success", "bottom");
                  $('#DatasTable').bootstrapTable('load', []);
                  $('#ViewsTable').bootstrapTable('load', []);
                  forceLogout();
              },
                error: function(data) {
                    console.log(data);
                    showalert("ERROR", "Unknown", "alert-danger", "bottom");
            
                }
            
              });            }
        },
        save: {
            label: 'Backup current project',
            className: 'btn-success',
            callback: function(){
              $("#dlPrj").trigger('click');             
            }
        }
    }
  })



})

$("#rmWks").click(function(){

  var message = [
    '<span style="font-size: 25px" class="glyphicon glyphicon-warning-sign" aria-hidden="true"></span>',
    '&nbsp;&nbsp;&nbsp;&nbsp; All your data will be dropped. Do you really want to clean your room ?'
  ].join("");

  bootbox.dialog({ 
    title: 'Remove workspace',
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
        remove: {
            label: 'Remove workspace',
            className: 'btn-warning',
            callback: function(){
              $.ajax({
                type: 'POST',
                url: "RMWks",
                dataType: 'json',
            
                success: function(data) {
                  console.log(data);
                  showalert(data.FROM, data.MESSAGE, "alert-success", "bottom");
                  $('#DatasTable').bootstrapTable('load', []);
                  $('#ViewsTable').bootstrapTable('load', []);
                  forceLogout();
              },
                error: function(data) {
                    console.log(data);
                    showalert("ERROR", "Unknown", "alert-danger", "bottom");
                }
            
              });
            }
        },
        save: {
            label: 'Backup workspace',
            className: 'btn-success',
            callback: function(){
              $("#dlWks").trigger('click');             
            }
        }
    }
  })

})


$("#ulPrj").click(function(){
  $("#ulPrjFile").trigger('click');
})

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
      console.log(data);
      if(data.STATUS == "OK"){
        showalert(data.FROM, data.MESSAGE, "alert-success", "bottom");
        $('#DatasTable').bootstrapTable('load', []);
        $('#ViewsTable').bootstrapTable('load', []);
        var message = '<br><br>Then open project :' +
        '<br><h3><span class="label label-primary">' + data.DATAS.name + '</span></h3>'      
        forceLogout(message);
      }
      else{
        showalert(data.FROM, data.MESSAGE, "alert-warning", "bottom");
      }
		},
		error: function(data) {
      console.log(data);
      showalert("ERROR", "Unknown", "alert-danger", "bottom");
		}
  });

  $(this).val('');  

})

$("#metrics").click(function(){

  $datasTable.bootstrapTable("filterBy", {});
  var qss = $datasTable.bootstrapTable('getData');
  var view = $('#ViewsTable').bootstrapTable("getData");

  if (qss.length == 0) {
    showalert("GetMetrics()", "No query subject loaded.", "alert-warning", "bottom");
    return;
  }
  
  var parms = { 
    qss: JSON.stringify(qss),
    view: JSON.stringify(view)
  };
  console.log(parms);

  $.ajax({
		type: 'POST',
		url: "GetMetrics",
		dataType: 'json',
    data: JSON.stringify(parms),

		success: function(data) {
      console.log(data.DATAS);

      /*
      {"qsCount":108,"finCount":24,"refCount":172,"dimensionCount":82,"measureCount":25,"customCount":88}
      */

      var jsonObj = data.DATAS;
      var metricsObj = JSON.parse(jsonObj);
      var qsCount = metricsObj.qsCount;
      var qsCountLabel = "QuerySubjectCount"
      var finCount = metricsObj.finCount;
      var finCountLabel = "RelationFinalCount"
      var refCount = metricsObj.refCount;
      var refCountLabel = "RelationRefCount"
      var dimensionCount = metricsObj.dimensionCount;
      var dimensionCountLabel = "LevelDimensionCount"
      var measureCount = metricsObj.measureCount;
      var measureCountLabel = "MeasureCount"
      var customCount = metricsObj.customCount;
      var customCountLabel = "CustomFieldCount"

      var list = '<ul class="list-group">';
          list += '<li class="list-group-item">' + qsCountLabel + '<span class="badge badge-primary">+ ' + qsCount + '</span></li>';
          list += '<li class="list-group-item">' + finCountLabel + '<span class="badge badge-primary">+ ' + finCount + '</span></li>';
          list += '<li class="list-group-item">' + refCountLabel + '<span class="badge badge-primary">+ ' + refCount + '</span></li>';
          list += '<li class="list-group-item">' + dimensionCountLabel + '<span class="badge badge-primary">+ ' + dimensionCount + '</span></li>';
          list += '<li class="list-group-item">' + measureCountLabel + '<span class="badge badge-primary">+ ' + measureCount + '</span></li>';
          list += '<li class="list-group-item">' + customCountLabel + '<span class="badge badge-primary">+ ' + customCount + '</span></li>';
      list += '</ul>';

      bootbox.alert({
        title: "Metrics :",
        message: list,
        callback: function(result){
        }
      });        

		},
		error: function(data) {
      console.log(data);
		}
	});

})


$("#settings").click(function(){

  $.ajax({
		type: 'POST',
		url: "GetCurrentProject",
		dataType: 'json',

		success: function(data) {
      console.log(data);
      var prj = data.data;
      $("#settingModal").modal("toggle");

      $("#prjname").val(prj.name);
      $("#prjlanguages").val(prj.languages[0]);
      $("#prjdbSchema").val(prj.dbSchema);
      $("#prjdescription").val(prj.description);
      $("#prjtimestamp").val(prj.timestamp);
      $("#prjrelationCount").val(prj.relationCount);

      $("#prjjndiName").val(prj.resource.jndiName);
      $("#prjdbName").val(prj.resource.dbName);
      $("#prjTableType").val(prj.resource.tableTypes);
      $("#prjdbEngine").val(prj.resource.dbEngine);
      $("#prjresdescription").val(prj.resource.description);
      $("#prjcognosCatalog").val(prj.resource.cognosCatalog);
      $("#prjcognosSchema").val(prj.resource.cognosSchema);
      $("#prjcognosDataSource").val(prj.resource.cognosDataSource);
		},
		error: function(data) {
      console.log(data);
		}
	});

})

$("#testModelDirConnection").click(function(){
  $.ajax({
    type: 'POST',
    url: "TestCognosModelsPathWriteable",
    dataType: 'json',

    success: function(data) {
      console.log(data);
      if(data.STATUS == "OK"){
        showalert(data.FROM, data.MESSAGE, "alert-success", "bottom");
      }
      else{
        showalert(data.FROM, data.MESSAGE, "alert-warning", "bottom");
      }
  },
    error: function(data) {
        console.log(data);
        showalert("ERROR", "Unknown", "alert-danger", "bottom");
    }

  });
})

$("#dropModels").click(function(){

  console.log($("#modelsSelect").val());

  var list = '<ul class="list-group">';
  $.each($("#modelsSelect").val(), function(i, mdl){
    list += '<li class="list-group-item">' + mdl + '</li>';
  });
  list += '</ul>';


  bootbox.confirm({
    title: "Following models will be dropped :",
    message: list,
    buttons: {
      cancel: {
          label: 'Cancel',
          className: 'btn btn-default'
      },
      confirm: {
          label: 'Drop',
          className: 'btn btn-primary'
      }
    },
    callback: function(result){

      if(!result){
        return;
      }

      var parms = {"models": $("#modelsSelect").val()};
      console.log(parms);
      
      $.ajax({
          type: 'POST',
          url: "DropModels",
          dataType: 'json',
          data: JSON.stringify(parms),
  
          success: function(data) {
            console.log(data);
            if(data.STATUS == "OK"){
              showalert(data.FROM, data.MESSAGE, "alert-success", "bottom")
            }
            else{
              showalert(data.FROM, data.MESSAGE, "alert-warning", "bottom")
            }
          },
          error: function(data) {
              console.log(data);
              showalert("DropModels", "an unknown error occured", "alert-danger", "bottom");
          }
  
      });
      $("#removeModelsModal").modal("toggle");
    }
  });


})

$("#rmModel").click(function(){

  $.ajax({
    type: 'POST',
    url: "GetModelList",
    dataType: 'json',

    success: function(data) {
      console.log(data);
      if(data.length > 0){
        $("#removeModelsModal").modal("toggle");
        $("#modelsSelect").selectpicker('deselectAll');
        $("#modelsSelect").empty();
        $.each(data, function(index, model){
          var option = '<option class="fontsize" value="' + model.name + '">' + model.name + '</option>';
          $("#modelsSelect").append(option);
        })
        $("#modelsSelect").selectpicker('refresh');
      }
      else{
        showalert("GetModelList", "No model found on server", "alert-warning", "bottom");
      }
  },
    error: function(data) {
        console.log(data);
        showalert("GetModelList", "an unknown occured", "alert-danger", "bottom");
    }

  });


})

$("#testCognosConnection").click(function(){
  $.ajax({
    type: 'POST',
    url: "TestCognosConnection",
    dataType: 'json',

    success: function(data) {
      console.log(data);
      if(data.STATUS == "OK"){
        showalert(data.FROM, data.MESSAGE, "alert-success", "bottom");
      }
      else{
        showalert(data.FROM, data.MESSAGE, "alert-warning", "bottom");
      }
  },
    error: function(data) {
        console.log(data);
        showalert("ERROR", "Unknown", "alert-danger", "bottom");
    }

  });

})

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
        showalert(data.FROM, data.MESSAGE, "alert-success", "bottom");
        $('#DatasTable').bootstrapTable('load', []);
        $('#ViewsTable').bootstrapTable('load', []);
        forceLogout();
      }
      else{
        showalert(data.FROM, data.MESSAGE, "alert-warning", "bottom");
      }
		},
		error: function(data) {
      console.log(data);
      showalert("ERROR", "Unknown", "alert-danger", "bottom");
		}
  });

  $(this).val('');  

})

$("#dlPrj").click(function(){

  $.ajax({
    type: 'POST',
    url: "ZipPRJ",
    dataType: 'json',

    success: function(data) {
      console.log(data);
      showalert(data.FROM, data.MESSAGE + "<br>Your download is about to begin...", "alert-success", "bottom");
      window.location.href = "DLPrj";
  },
    error: function(data) {
        console.log(data);
        showalert("ERROR", "Unknown", "alert-danger", "bottom");

    }

  });
})


$("#dlWks").click(function(){
  $.ajax({
    type: 'POST',
    url: "ZipWKS",
    dataType: 'json',

    success: function(data) {
      console.log(data);
      showalert(data.FROM, data.MESSAGE + "<br>Your download is about to begin...", "alert-success", "bottom");
      window.location.href = "DLWks";
  },
    error: function(data) {
        console.log(data);
        showalert("ERROR", "Unknown", "alert-danger", "bottom");

    }

  });
})

$('#relsQueryModal').on('shown.bs.modal', function() {

  $.ajax({
 		type: 'POST',
 		url: "GetRelationQuery",
 		dataType: 'json',

 		success: function(data) {
      console.log(data)
      if(data.DATAS){
        $("#FKQuery").val(data.DATAS.FKQuery);
        $("#PKQuery").val(data.DATAS.PKQuery);
      }
 		},
 		error: function(data) {
      console.log(data)
 			ShowAlert("Getting relations queries failed.", "alert-danger", $("#relsQueryModalAlert"));
 		}
 	});


});

$('#relsQueryModal').on('hidden.bs.modal', function() {
    $('#searchSelect').selectpicker('deselectAll');
    $('#relsQueryModal .collapse').each(function () {
        $(this).collapse('hide');
    });
    $("#FKQuery").val('');
    $("#PKQuery").val('');
});

function saveRelsQuery(){

  $('#relsQueryModal .collapse').each(function () {
      $(this).collapse('hide');
  });

  var FKQuery = $("#FKQuery").val().replace(/[^\x20-\x7E]/gmi, "");
  var PKQuery = $("#PKQuery").val().replace(/[^\x20-\x7E]/gmi, "");
  var parms = {"FKQuery" : FKQuery, "PKQuery": PKQuery};
  console.log(parms);

 	$.ajax({
 		type: 'POST',
 		url: "SaveRelationQuery",
 		dataType: 'json',
 		data: JSON.stringify(parms),

 		success: function(data) {
      console.log(data)
 			ShowAlert(data.MESSAGE, "alert-" + data.ALERT, $("#relsQueryModalAlert"));
      $('#searchSelect').selectpicker('unSelectAll');
 		},
 		error: function(data) {
      console.log(data)
 			ShowAlert("Saving Query failed.", "alert-danger", $("#relsQueryModalAlert"));
 		}
 	});

}

$("#ulModelFile").change(function(){
  var fd = new FormData();

  var file = $(this)[0].files[0];
  // console.log(file);
  // var fileName = file.name;

  fd.append('file', file, file.name);
  // console.log(fd);

  $.ajax({
    url: "UploadModel",
    type: "POST",
    data: fd,
    enctype: 'multipart/form-data',
    // dataType: 'application/text',
    processData: false,  // tell jQuery not to process the data
    contentType: false,   // tell jQuery not to set contentType
		success: function(data) {
    console.log(data);
      if(data.STATUS == "OK" && data.DATAS != null){
        if(data.DATAS.querySubjects){
          console.log(data.DATAS.querySubjects);
          $datasTable.bootstrapTable("load", data.DATAS.querySubjects);
        }
        if(data.DATAS.views){
          $("#viewTab").removeClass('disabled');
          $viewTab.prop('disabled',false);
          $("#ViewsTable").bootstrapTable("load", data.DATAS.views);
        }
        // $refTab.tab('show');
        initGlobals();
        // $finTab.tab('show');
        // $qsTab.tab('show');
        $("#qsTab_click").click();
        if(data.DATAS.querySubjects){
          var langs = Object.keys(data.DATAS.querySubjects[0].labels);
          console.log(langs[0]);
          $("#langSelect").selectpicker('val', langs[0]);
          $("#langSelect").selectpicker('refresh');
          SetLanguage(langs[0]);
        }

        $("#qsSelect").empty();
        $.each($datasTable.bootstrapTable("getData"), function(i, data){
          if(data.type.match("Final|Ref")){
            var option = '<option class="fontsize" value="' + data._id + '" data-subtext="' + data.type + '" data-content="">' + data._id + '</option>';
            $("#qsSelect").append(option);
          }
        })
        $("#qsSelect").selectpicker('refresh');
      }
		},
		error: function(data) {
      console.log(data);
      showalert("ERROR", "Unknown", "alert-danger", "bottom");
		}
  });

  $(this).val('');  

})


$("#ulModel").click(function(){

  if($datasTable.bootstrapTable("getData").length == 0){
    $("#ulModelFile").trigger('click');
    return;
  }

  var message = [
    '<span style="font-size: 25px" class="glyphicon glyphicon-warning-sign" aria-hidden="true"></span>',
    '&nbsp;&nbsp;&nbsp;&nbsp; Current model will be dropped.'
  ].join("");

  bootbox.dialog({ 
    title: 'Upload model',
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
            label: 'Upload',
            className: 'btn-warning',
            callback: function(){
               $("#ulModelFile").trigger('click');                 
            }
        },
        save: {
            label: 'Save current model',
            className: 'btn-success',
            callback: function(){
              SaveModel();             
            }
        }
    }
  })

})

$("#dlModel").click(function(){

  $datasTable.bootstrapTable("filterBy", {});
  var qss = $datasTable.bootstrapTable('getData');
  var view = $('#ViewsTable').bootstrapTable("getData");

  if (qss.length == 0) {
    showalert("SaveModel()", "Nothing to save.", "alert-warning", "bottom");
    return;
  }
  
  var parms = { 
    qss: JSON.stringify(qss),
    view: JSON.stringify(view)
  };
  console.log(parms);

   $.ajax({
     type: 'POST',
     url: "DownloadModel",
     dataType: 'json',
     data: JSON.stringify(parms),

     success: function(data) {
      console.log(data);
      if(data.STATUS == "OK"){
        showalert(data.FROM, data.MESSAGE + "<br>Your download is about to begin...", "alert-success", "bottom");
        window.location.href = "DLModel";
      }
      else{
        showalert(data.FROM, data.MESSAGE, "alert-warning", "bottom");
      }
     },
     error: function(data) {
       console.log(data);
      showalert("ERROR", "Unknown","alert-danger", "bottom");
     }
   });


})

$("#saveRel").click(function(){

  $.ajax({
    type: 'POST',
    url: "SaveRelations",
    dataType: 'json',

    success: function(data) {
      console.log(data);
      if(data.STATUS == "OK"){
        showalert(data.FROM, data.MESSAGE + "<br>Your download is about to begin...", "alert-success", "bottom");
        window.location.href = "DownloadRelations";
      }
      else{
        showalert(data.FROM, data.MESSAGE, "alert-warning", "bottom");
      }
    },
    error: function(data) {
      console.log(data);
      showalert("ERROR", "Unknown","alert-danger", "bottom");
    }
  });  

})

sortQss.addEventListener('click', function(event){

  $datasTable.bootstrapTable("filterBy", {});

  var qss = $datasTable.bootstrapTable("getData");

  if(qss.length > 0){
    $.each(qss, function(i, qs){
      qs.fields.sort((a, b) => parseInt(a.fieldPos) - parseInt(b.fieldPos));
    })
  }

  event.preventDefault();
}, false);

downloadQss.addEventListener('click', function(event){

  var qss = $datasTable.bootstrapTable("getData");

  if(qss.length == 0){
    showalert("Nothing to do", "No Query Subjects found.", "alert-info", "bottom");
    return;
  }

  var lang = $("#langSelect").find("option:selected").val();

  var parms = {"qss": JSON.stringify(qss), "delim": ";", "lang": lang}

  console.log(parms);

  $.ajax({
    type: 'POST',
    url: "SaveQss",
    dataType: 'json',
    data: JSON.stringify(parms),

    success: function(data) {
      console.log(data);
      if(data.STATUS == "OK"){
        showalert(data.FROM, data.MESSAGE, "alert-success", "bottom");
        window.location.href = "DownloadQssAsCSV";
      }
      else{
        showalert(data.FROM, data.MESSAGE, "alert-warning", "bottom");
      }
    },
    error: function(data) {
      console.log(data);
    }
  });
  
  event.preventDefault();
}, false);


downloadViews.addEventListener('click', function(event){

  var vues = $('#ViewsTable').bootstrapTable("getData");

  if(vues.length == 0){
    showalert("Nothing to do", "No view found.", "alert-info", "bottom");
    return;
  }

  var lang = $("#langSelect").find("option:selected").val();

  var parms = {"views": JSON.stringify(vues), "delim": ";", "lang": lang}

  console.log(JSON.stringify(vues));
  console.log(parms);

  $.ajax({
    type: 'POST',
    url: "SaveViews",
    dataType: 'json',
    data: JSON.stringify(parms),

    success: function(data) {
      console.log(data);
      if(data.STATUS == "OK"){
        showalert(data.FROM, data.MESSAGE, "alert-success", "bottom");
        window.location.href = "DownloadViewsAsCSV";
      }
      else{
        showalert(data.FROM, data.MESSAGE, "alert-warning", "bottom");
      }
    },
    error: function(data) {
      console.log(data);
    }
  });
  
  event.preventDefault();
}, false);


removeRel.addEventListener('click', function(event){

  bootbox.confirm({
    title: "Removing relations.",
    message: "Both SQL and CSV Relations will be dropped. Do you confirm ?",
    buttons: {
      cancel: {
          label: 'Cancel',
          className: 'btn btn-default'
      },
      confirm: {
          label: 'Confirm',
          className: 'btn btn-warning'
      }
    },
    callback: function(result){
      if(result){
        $.ajax({
      		type: 'POST',
      		url: "RemoveRelationQuery",
      		dataType: 'json',

      		success: function(data) {
            console.log(data);
            $("#FKQuery").val('');
            $("#PKQuery").val('');
            showalert("RemoveRelationQuery()", "Removing relations successfully.", "alert-success", "bottom");
      		},
      		error: function(data) {
            console.log(data);
      			showalert("RemoveRelationQuery()", "Removing relations query failed.", "alert-danger", "bottom");
      		}
      	});

      }
    }
  });

  event.preventDefault();
}, false);

runFKQuery.addEventListener('click', function(event){
  var query = $("#FKQuery").val().replace(/[^\x20-\x7E]/gmi, "");

  if(query.trim().length == 0){
    ShowAlert("Query is empty.", "alert-warning", $("#relsQueryModalAlert"));
    return;
  }
  var type = 'relation';

  var tables = [];
  $('#tables option').each(function(){
    tables.push($(this).val());
  })

  BuildTestQuery(query, type, tables);

  event.preventDefault();
}, false);

eraseFKQuery.addEventListener('click', function(event){
  $("#FKQuery").val('');
  event.preventDefault();
}, false);

runPKQuery.addEventListener('click', function(event){
  var query = $("#PKQuery").val().replace(/[^\x20-\x7E]/gmi, "");
  if(query.trim().length == 0){
    ShowAlert("Query is empty.", "alert-warning", $("#relsQueryModalAlert"));
    return;
  }
  var type = 'relation';

  var tables = [];
  $('#tables option').each(function(){
    tables.push($(this).val());
  })

  BuildTestQuery(query, type, tables);

  event.preventDefault();
}, false);

erasePKQuery.addEventListener('click', function(event){
  $("#PKQuery").val('');
  event.preventDefault();
}, false);

tableLabelQuery.addEventListener('click', function(event){
  var query = $("#tableLabel").val().replace(/[^\x20-\x7E]/gmi, "");
  if(query.trim().length == 0){
    ShowAlert("Query is empty.", "alert-warning", $("#tableLabelAlert"));
    return;
  }
  var type = 'table';

  var tables = new Set();
  if($datasTable.bootstrapTable("getData").length > 0){
    var qss = $datasTable.bootstrapTable("getData");
    $.each(qss, function(i, qs){
      tables.add(qs.table_name);
    })
  }
  tables = Array.from(tables);

  if(tables.length == 0){
    ShowAlert("Import at least one Query Subject to test.", "alert-warning", $("#tableLabelAlert"));
  }
  else{
    BuildTestQuery(query, type, tables);
  }

  event.preventDefault();
}, false);

eraseTableLabelQuery.addEventListener('click', function(event){
  $("#tableLabel").val('');
  event.preventDefault();
}, false);

// A MODIFIER dans myfunction.js
tableDescriptionQuery.addEventListener('click', function(event){
  var query = $("#tableDescription").val().replace(/[^\x20-\x7E]/gmi, "");
  if(query.trim().length == 0){
    ShowAlert("Query is empty.", "alert-warning", $("#tableDescriptionAlert"));
    return;
  }
  var type = 'table';

  var tables = new Set();
  if($datasTable.bootstrapTable("getData").length > 0){
    var qss = $datasTable.bootstrapTable("getData");
    $.each(qss, function(i, qs){
      tables.add(qs.table_name);
    })
  }
  tables = Array.from(tables);

  if(tables.length == 0){
    ShowAlert("Import at least one Query Subject to test.", "alert-warning", $("#tableDescriptionAlert"));
  }
  else{
    BuildTestQuery(query, type, tables);
  }
  event.preventDefault();
}, false);

eraseTableDescriptionQuery.addEventListener('click', function(event){
  $("#tableDescription").val('');
  event.preventDefault();
}, false);

columnLabelQuery.addEventListener('click', function(event){
  var query = $("#columnLabel").val().replace(/[^\x20-\x7E]/gmi, "");
  if(query.trim().length == 0){
    ShowAlert("Query is empty.", "alert-warning", $("#columnLabelAlert"));
    return;
  }
  var type = 'column';
  var tables = new Set();
  if($datasTable.bootstrapTable("getData").length > 0){
    var qss = $datasTable.bootstrapTable("getData");
    $.each(qss, function(i, qs){
      tables.add(qs.table_name);
    })
  }
  tables = Array.from(tables);

  if(tables.length == 0){
    ShowAlert("Import at least one Query Subject to test.", "alert-warning", $("#columnLabelAlert"));
  }
  else{
    BuildTestQuery(query, type, tables);
  }
  event.preventDefault();
}, false);

eraseColumnLabelQuery.addEventListener('click', function(event){
  $("#columnLabel").val('');
  event.preventDefault();
}, false);

columnDescriptionQuery.addEventListener('click', function(event){
  var query = $("#columnDescription").val().replace(/[^\x20-\x7E]/gmi, "");
  if(query.trim().length == 0){
    ShowAlert("Query is empty.", "alert-warning", $("#columnDescriptionAlert"));
    return;
  }
  var type = 'column';
  var tables = new Set();
  if($datasTable.bootstrapTable("getData").length > 0){
    var qss = $datasTable.bootstrapTable("getData");
    $.each(qss, function(i, qs){
      tables.add(qs.table_name);
    })
  }
  tables = Array.from(tables);

  if(tables.length == 0){
    ShowAlert("Import at least one Query Subject to test.", "alert-warning", $("#columnDescriptionAlert"));
  }
  else{
    BuildTestQuery(query, type, tables);
  }
  event.preventDefault();
}, false);

eraseColumnDescriptionQuery.addEventListener('click', function(event){
  $("#columnDescription").val('');
  event.preventDefault();
}, false);



// END
