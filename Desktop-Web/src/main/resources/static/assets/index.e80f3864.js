import{r as d,o as f,c as p,a as m,b as _,d as h}from"./vendor.ccd5eab0.js";const y=function(){const s=document.createElement("link").relList;if(s&&s.supports&&s.supports("modulepreload"))return;for(const e of document.querySelectorAll('link[rel="modulepreload"]'))r(e);new MutationObserver(e=>{for(const t of e)if(t.type==="childList")for(const o of t.addedNodes)o.tagName==="LINK"&&o.rel==="modulepreload"&&r(o)}).observe(document,{childList:!0,subtree:!0});function n(e){const t={};return e.integrity&&(t.integrity=e.integrity),e.referrerpolicy&&(t.referrerPolicy=e.referrerpolicy),e.crossorigin==="use-credentials"?t.credentials="include":e.crossorigin==="anonymous"?t.credentials="omit":t.credentials="same-origin",t}function r(e){if(e.ep)return;e.ep=!0;const t=n(e);fetch(e.href,t)}};y();var v=(c,s)=>{const n=c.__vccOpts||c;for(const[r,e]of s)n[r]=e;return n};const g={};function L(c,s){const n=d("router-view");return f(),p(n)}var O=v(g,[["render",L]]);const b="modulepreload",i={},k="/",E=function(s,n){return!n||n.length===0?s():Promise.all(n.map(r=>{if(r=`${k}${r}`,r in i)return;i[r]=!0;const e=r.endsWith(".css"),t=e?'[rel="stylesheet"]':"";if(document.querySelector(`link[href="${r}"]${t}`))return;const o=document.createElement("link");if(o.rel=e?"stylesheet":b,e||(o.as="script",o.crossOrigin=""),o.href=r,document.head.appendChild(o),e)return new Promise((a,u)=>{o.addEventListener("load",a),o.addEventListener("error",u)})})).then(()=>s())},P=[{path:"/",name:"Index",component:()=>E(()=>import("./index.cd0a7664.js"),["assets/index.cd0a7664.js","assets/index.b5c20f1f.css","assets/vendor.ccd5eab0.js"])}],x=m({history:_(),routes:P}),l=h(O);l.use(x);l.mount("#app");
