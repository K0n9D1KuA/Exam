(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-7cc79236"],{"690d":function(e,t,a){"use strict";a.r(t);var n=function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("div",{staticClass:"mod-config"},[a("el-form",{attrs:{inline:!0,model:e.dataForm},nativeOn:{keyup:function(t){return!t.type.indexOf("key")&&e._k(t.keyCode,"enter",13,t.key,"Enter")?null:e.getDataList()}}},[a("el-form-item",[a("el-input",{attrs:{placeholder:"参数名",clearable:""},model:{value:e.dataForm.key,callback:function(t){e.$set(e.dataForm,"key",t)},expression:"dataForm.key"}})],1),a("el-form-item",[a("el-button",{on:{click:function(t){return e.getDataList()}}},[e._v("查询")])],1),a("el-form-item",[a("el-button",{attrs:{type:"success"}},[e._v("提示:管理员不能修改删除试卷")])],1)],1),a("el-table",{directives:[{name:"loading",rawName:"v-loading",value:e.dataListLoading,expression:"dataListLoading"}],staticStyle:{width:"100%"},attrs:{data:e.dataList,border:""},on:{"selection-change":e.selectionChangeHandle}},[a("el-table-column",{attrs:{prop:"paperName","header-align":"center",align:"center",label:"试卷名称"}}),a("el-table-column",{attrs:{prop:"beginTime","header-align":"center",align:"center",label:"开始时间"}}),a("el-table-column",{attrs:{prop:"endTime","header-align":"center",align:"center",label:"结束时间"}}),a("el-table-column",{attrs:{prop:"totalTime","header-align":"center",align:"center",label:"总共持续时间 以分钟为单位"}}),a("el-table-column",{attrs:{prop:"totalScore","header-align":"center",align:"center",label:"总分"}}),a("el-table-column",{attrs:{prop:"teacherName","header-align":"center",align:"center",label:"出题老师"}}),a("el-table-column",{attrs:{fixed:"right","header-align":"center",align:"center",width:"150",label:"操作"},scopedSlots:e._u([{key:"default",fn:function(t){return[a("el-button",{attrs:{type:"text",size:"small"},on:{click:function(a){return e.addOrUpdateHandle(t.row.id)}}},[e._v("预览试卷")])]}}])})],1),a("el-pagination",{attrs:{"current-page":e.pageIndex,"page-sizes":[10,20,50,100],"page-size":e.pageSize,total:e.totalPage,layout:"total, sizes, prev, pager, next, jumper"},on:{"size-change":e.sizeChangeHandle,"current-change":e.currentChangeHandle}})],1)},i=[],o=(a("d81d"),a("99af"),a("a15b"),{components:{},data:function(){return{token:"",dataForm:{key:""},dataList:[],pageIndex:1,pageSize:10,totalPage:0,dataListLoading:!1,dataListSelections:[],addOrUpdateVisible:!1}},computed:{},watch:{},methods:{getDataList:function(){var e=this;this.dataListLoading=!0,this.$http.get("courseSystem/paper/getTotalSchoolPaper",{params:{page:this.pageIndex,pageSize:this.pageSize,key:this.dataForm.key},headers:{token:this.token}}).then((function(t){var a=t.data;a&&0===a.code?(e.dataList=a.page.list,e.totalPage=a.page.totalCount):(e.dataList=[],e.totalPage=0),e.dataListLoading=!1}))},sizeChangeHandle:function(e){this.pageSize=e,this.pageIndex=1,this.getDataList()},currentChangeHandle:function(e){this.pageIndex=e,this.getDataList()},selectionChangeHandle:function(e){this.dataListSelections=e},addOrUpdateHandle:function(e){var t=this;this.addOrUpdateVisible=!0,this.$nextTick((function(){t.$refs.addOrUpdate.init(e)}))},deleteHandle:function(e){var t=this,a=e?[e]:this.dataListSelections.map((function(e){return e.id}));this.$confirm("确定对[id=".concat(a.join(","),"]进行[").concat(e?"删除":"批量删除","]操作?"),"提示",{confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"}).then((function(){t.$http({url:t.$http.adornUrl("//paper/delete"),method:"post",data:t.$http.adornData(a,!1)}).then((function(e){var a=e.data;a&&0===a.code?t.$message({message:"操作成功",type:"success",duration:1500,onClose:function(){t.getDataList()}}):t.$message.error(a.msg)}))}))}},created:function(){this.token=this.$store.state.user.token,console.log(this.token),this.getDataList()},mounted:function(){},beforeCreate:function(){},beforeMount:function(){},beforeUpdate:function(){},updated:function(){},beforeDestroy:function(){},destroyed:function(){},activated:function(){}}),r=o,l=a("2877"),c=Object(l["a"])(r,n,i,!1,null,"215ccf43",null);t["default"]=c.exports},"99af":function(e,t,a){"use strict";var n=a("23e7"),i=a("da84"),o=a("d039"),r=a("e8b5"),l=a("861d"),c=a("7b0b"),s=a("07fa"),d=a("8418"),u=a("65f0"),p=a("1dde"),g=a("b622"),f=a("2d00"),h=g("isConcatSpreadable"),m=9007199254740991,b="Maximum allowed index exceeded",k=i.TypeError,v=f>=51||!o((function(){var e=[];return e[h]=!1,e.concat()[0]!==e})),y=p("concat"),L=function(e){if(!l(e))return!1;var t=e[h];return void 0!==t?!!t:r(e)},x=!v||!y;n({target:"Array",proto:!0,forced:x},{concat:function(e){var t,a,n,i,o,r=c(this),l=u(r,0),p=0;for(t=-1,n=arguments.length;t<n;t++)if(o=-1===t?r:arguments[t],L(o)){if(i=s(o),p+i>m)throw k(b);for(a=0;a<i;a++,p++)a in o&&d(l,p,o[a])}else{if(p>=m)throw k(b);d(l,p++,o)}return l.length=p,l}})},a15b:function(e,t,a){"use strict";var n=a("23e7"),i=a("e330"),o=a("44ad"),r=a("fc6a"),l=a("a640"),c=i([].join),s=o!=Object,d=l("join",",");n({target:"Array",proto:!0,forced:s||!d},{join:function(e){return c(r(this),void 0===e?",":e)}})}}]);
//# sourceMappingURL=chunk-7cc79236.a0ee0820.js.map