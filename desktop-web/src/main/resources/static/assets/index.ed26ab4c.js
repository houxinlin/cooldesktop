import{r as p,o as m,c as h,a as g,b as _,v as y,d as v,e as w,m as b}from"./vendor.42559af5.js";const $=function(){const o=document.createElement("link").relList;if(o&&o.supports&&o.supports("modulepreload"))return;for(const t of document.querySelectorAll('link[rel="modulepreload"]'))r(t);new MutationObserver(t=>{for(const s of t)if(s.type==="childList")for(const i of s.addedNodes)i.tagName==="LINK"&&i.rel==="modulepreload"&&r(i)}).observe(document,{childList:!0,subtree:!0});function n(t){const s={};return t.integrity&&(s.integrity=t.integrity),t.referrerpolicy&&(s.referrerPolicy=t.referrerpolicy),t.crossorigin==="use-credentials"?s.credentials="include":t.crossorigin==="anonymous"?s.credentials="omit":s.credentials="same-origin",s}function r(t){if(t.ep)return;t.ep=!0;const s=n(t);fetch(t.href,s)}};$();var x=(e,o)=>{const n=e.__vccOpts||e;for(const[r,t]of o)n[r]=t;return n};const A={};function C(e,o){const n=p("router-view");return m(),h(n)}var k=x(A,[["render",C]]);const I="modulepreload",l={},L="/",O=function(o,n){return!n||n.length===0?o():Promise.all(n.map(r=>{if(r=`${L}${r}`,r in l)return;l[r]=!0;const t=r.endsWith(".css"),s=t?'[rel="stylesheet"]':"";if(document.querySelector(`link[href="${r}"]${s}`))return;const i=document.createElement("link");if(i.rel=t?"stylesheet":I,t||(i.as="script",i.crossOrigin=""),i.href=r,document.head.appendChild(i),t)return new Promise((f,d)=>{i.addEventListener("load",f),i.addEventListener("error",d)})})).then(()=>o())},E=[{path:"/",name:"Index",component:()=>O(()=>import("./index.a2160b3d.js"),["assets/index.a2160b3d.js","assets/index.fa7b1125.css","assets/vendor.42559af5.js"])}],P=g({history:_(),routes:E});let u={};function B(e){alert("a"),u=e}function N(e){return u[e]}const c="s",M="logo.png";function R(e,o=!0){v(e,void 0,(n,r)=>{}),!!o&&postMessage({action:"notification",param:{message:"\u590D\u5236\u6210\u529F",type:"success"}},"*")}function T(){return y()}function K(e,o=null,n=""){let r=o!=null?`?${o}`:"";if(e.type==3)return`${c}${e.applicationId}/${n}${r}`;let t=`${c}desktop/webapplication/${e.applicationId}/index.html`;return o!=null&&(t=t+"?"+o),t}function j(e){return`${c}desktop/webapplication/${e.applicationId}/${M}`}const q=e=>e<1024?e+"\u5B57\u8282":e/1024<1024?Math.floor(e/1024)+"KB":Math.round(e/1024/1024)+"MB",D=e=>(e&4294901760)>>16,F=e=>e&65535;window.global=window;const a=w(k);a.config.globalProperties.eventBus=new b;addressConfig={},addressConfig.host=location.protocol+"//"+location.host+"/",addressConfig.websocket=location.protocol+"//"+location.host+"/wbs",addressConfig.softwareServer="http://www.houxinlin.com:8081/",addressConfig.terminalSocket="ws://"+location.host+"/ws/websocket/terminal",B(addressConfig);a.use(P);a.mount("#app");export{j as a,N as b,R as c,K as g,D as h,F as l,T as r,q as s};
