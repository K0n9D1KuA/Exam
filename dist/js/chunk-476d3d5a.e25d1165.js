(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-476d3d5a"],{"0183":function(t,e,n){"use strict";n("9134")},"0844":function(t,e,n){},5142:function(t,e,n){"use strict";n.r(e);var r=function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("el-container",{staticStyle:{height:"100%"}},[n("el-aside",{attrs:{width:"auto"}},[n("common-aside")],1),n("el-container",[n("el-header",[n("common-header")],1),n("common-tag"),n("el-main",[n("router-view")],1)],1)],1)},o=[],s=function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("el-menu",{staticClass:"el-menu-vertical-demo",attrs:{"default-active":"1-4-1","background-color":"#545c64","text-color":"#fff","active-text-color":"#ffd04b",collapse:t.isCollapse},on:{open:t.handleOpen,close:t.handleClose}},[n("h3",[t._v(t._s(t.isCollapse?"后台":"在线考试系统后台"))]),t._l(t.noChildren,(function(e){return n("el-menu-item",{key:e.path,attrs:{index:e.path},on:{click:function(n){return t.clickMenu(e)}}},[n("i",{class:"el-icon-"+e.icon}),n("span",{attrs:{slot:"title"},slot:"title"},[t._v(t._s(e.label))])])})),t._l(t.hasChildren,(function(e){return n("el-submenu",{key:e.label,attrs:{index:e.label}},[n("template",{slot:"title"},[n("i",{class:"el-icon-"+e.icon}),n("span",{attrs:{slot:"title"},slot:"title"},[t._v(t._s(e.label))])]),t._l(e.children,(function(e,r){return n("el-menu-item-group",{key:e.path},[n("el-menu-item",{attrs:{index:r.toString()},on:{click:function(n){return t.clickMenu(e)}}},[t._v(t._s(e.label))])],1)}))],2)}))],2)},a=[],i=(n("b0c0"),n("4de4"),n("d3b7"),{data:function(){return{menu:[]}},methods:{handleOpen:function(t,e){console.log(t,e)},handleClose:function(t,e){console.log(t,e)},clickMenu:function(t){this.$router.push({name:t.name}),this.$store.commit("selectMenu",t)}},computed:{noChildren:function(){return this.asyncMenu.filter((function(t){return!t.children}))},hasChildren:function(){return this.asyncMenu.filter((function(t){return t.children}))},isCollapse:function(){return this.$store.state.tab.isCollapse},asyncMenu:function(){return this.$store.state.tab.menu}}}),c=i,l=(n("93b4"),n("2877")),u=Object(l["a"])(c,s,a,!1,null,"742ed3b7",null),f=u.exports,d=function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("header",[n("div",{staticClass:"l-content"},[n("el-button",{attrs:{plain:"",icon:"el-icon-menu",size:"mini"},on:{click:t.handleMenu}}),n("el-breadcrumb",{attrs:{separator:"/"}},t._l(t.tags,(function(e){return n("el-breadcrumb-item",{key:e.path,attrs:{to:{path:e.path}}},[t._v(t._s(e.label))])})),1)],1),n("div",{staticClass:"r-content"},[n("el-dropdown",{attrs:{trigger:"click",size:"mini"}},[n("span",[n("img",{staticClass:"user",attrs:{src:t.userInfo.avatar}})]),n("el-dropdown-menu",{attrs:{slot:"dropdown"},slot:"dropdown"},[n("el-dropdown-item",{nativeOn:{click:function(e){return t.showPersonality.apply(null,arguments)}}},[t._v("个人中心")]),n("el-dropdown-item",{nativeOn:{click:function(e){return t.logOut.apply(null,arguments)}}},[t._v("退出")])],1)],1)],1)])},m=[],h=n("5530"),b=n("2f62"),p={name:"CommonHeader",data:function(){return{userInfo:{},userImg:n("ba97"),dialogVisible:!1}},methods:{showPersonality:function(){this.dialogVisible=!0},handleMenu:function(){this.$store.commit("collapseMenu")},logOut:function(){var t=this;this.$http.get("courseSystem/auth/logout",{headers:{token:this.$store.state.user.token}}).then((function(e){var n=e.data;0==n.code?(t.$store.commit("clearToken"),t.$store.commit("clearMenu"),t.$router.push("/login")):t.$message("退出登录失败")}))}},computed:Object(h["a"])({},Object(b["c"])({tags:function(t){return t.tab.tabsList}})),created:function(){this.$store.commit("getUserInfo"),this.userInfo=this.$store.state.user.userInfo,"teacher"===this.userInfo.role&&(this.userInfo.role="老师"),"admin"===this.userInfo.role&&(this.userInfo.role="管理员")}},g=p,O=(n("cd1a"),Object(l["a"])(g,d,m,!1,null,"3d04940b",null)),v=O.exports,y=function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("div",{staticClass:"tabs"},t._l(t.tags,(function(e,r){return n("el-tag",{key:e.name,attrs:{closable:"home"!==e.name,effect:t.$route.name===e.name?"dark":"plain",size:"small"},on:{click:function(n){return t.changeMenu(e)},close:function(n){return t.handleClose(e,r)}}},[t._v(" "+t._s(e.label)+" ")])})),1)},_=[],j={name:"CommonTag",data:function(){return{}},computed:Object(h["a"])({},Object(b["c"])({tags:function(t){return t.tab.tabsList}})),methods:Object(h["a"])(Object(h["a"])({},Object(b["b"])({close:"closeTag"})),{},{changeMenu:function(t){this.$router.push({name:t.name})},handleClose:function(t,e){var n=this.tags.length-1;this.close(t),t.name===this.$route.name&&(e===n?this.$router.push({name:this.tags[e-1].name}):this.$router.push({name:this.tags[e].name}))}})},w=j,k=(n("52bc"),Object(l["a"])(w,y,_,!1,null,"3d314631",null)),C=k.exports,$={name:"Home",components:{CommonAside:f,CommonHeader:v,CommonTag:C},data:function(){return{}}},M=$,P=(n("0183"),Object(l["a"])(M,r,o,!1,null,"26b05201",null));e["default"]=P.exports},"52bc":function(t,e,n){"use strict";n("0844")},5530:function(t,e,n){"use strict";n.d(e,"a",(function(){return s}));n("b64b"),n("a4d3"),n("4de4"),n("d3b7"),n("e439"),n("159b"),n("dbb4");function r(t,e,n){return e in t?Object.defineProperty(t,e,{value:n,enumerable:!0,configurable:!0,writable:!0}):t[e]=n,t}function o(t,e){var n=Object.keys(t);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(t);e&&(r=r.filter((function(e){return Object.getOwnPropertyDescriptor(t,e).enumerable}))),n.push.apply(n,r)}return n}function s(t){for(var e=1;e<arguments.length;e++){var n=null!=arguments[e]?arguments[e]:{};e%2?o(Object(n),!0).forEach((function(e){r(t,e,n[e])})):Object.getOwnPropertyDescriptors?Object.defineProperties(t,Object.getOwnPropertyDescriptors(n)):o(Object(n)).forEach((function(e){Object.defineProperty(t,e,Object.getOwnPropertyDescriptor(n,e))}))}return t}},"6f3f":function(t,e,n){},9134:function(t,e,n){},"93b4":function(t,e,n){"use strict";n("d4df")},b64b:function(t,e,n){var r=n("23e7"),o=n("7b0b"),s=n("df75"),a=n("d039"),i=a((function(){s(1)}));r({target:"Object",stat:!0,forced:i},{keys:function(t){return s(o(t))}})},ba97:function(t,e,n){t.exports=n.p+"img/user.27e729e0.png"},cd1a:function(t,e,n){"use strict";n("6f3f")},d4df:function(t,e,n){},dbb4:function(t,e,n){var r=n("23e7"),o=n("83ab"),s=n("56ef"),a=n("fc6a"),i=n("06cf"),c=n("8418");r({target:"Object",stat:!0,sham:!o},{getOwnPropertyDescriptors:function(t){var e,n,r=a(t),o=i.f,l=s(r),u={},f=0;while(l.length>f)n=o(r,e=l[f++]),void 0!==n&&c(u,e,n);return u}})},e439:function(t,e,n){var r=n("23e7"),o=n("d039"),s=n("fc6a"),a=n("06cf").f,i=n("83ab"),c=o((function(){a(1)})),l=!i||c;r({target:"Object",stat:!0,forced:l,sham:!i},{getOwnPropertyDescriptor:function(t,e){return a(s(t),e)}})}}]);
//# sourceMappingURL=chunk-476d3d5a.e25d1165.js.map