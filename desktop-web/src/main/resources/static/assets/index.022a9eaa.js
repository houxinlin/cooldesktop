import{r as f,o as d,c as p,a as m,b as _,d as h,m as v}from"./vendor.c5d1512f.js";const y=function(){const s=document.createElement("link").relList;if(s&&s.supports&&s.supports("modulepreload"))return;for(const e of document.querySelectorAll('link[rel="modulepreload"]'))r(e);new MutationObserver(e=>{for(const t of e)if(t.type==="childList")for(const o of t.addedNodes)o.tagName==="LINK"&&o.rel==="modulepreload"&&r(o)}).observe(document,{childList:!0,subtree:!0});function n(e){const t={};return e.integrity&&(t.integrity=e.integrity),e.referrerpolicy&&(t.referrerPolicy=e.referrerpolicy),e.crossorigin==="use-credentials"?t.credentials="include":e.crossorigin==="anonymous"?t.credentials="omit":t.credentials="same-origin",t}function r(e){if(e.ep)return;e.ep=!0;const t=n(e);fetch(e.href,t)}};y();var g=(i,s)=>{const n=i.__vccOpts||i;for(const[r,e]of s)n[r]=e;return n};const w={};function L(i,s){const n=f("router-view");return d(),p(n)}var b=g(w,[["render",L]]);const O="modulepreload",l={},P="/",k=function(s,n){return!n||n.length===0?s():Promise.all(n.map(r=>{if(r=`${P}${r}`,r in l)return;l[r]=!0;const e=r.endsWith(".css"),t=e?'[rel="stylesheet"]':"";if(document.querySelector(`link[href="${r}"]${t}`))return;const o=document.createElement("link");if(o.rel=e?"stylesheet":O,e||(o.as="script",o.crossOrigin=""),o.href=r,document.head.appendChild(o),e)return new Promise((a,u)=>{o.addEventListener("load",a),o.addEventListener("error",u)})})).then(()=>s())},E=[{path:"/",name:"Index",component:()=>k(()=>import("./index.4e8f928a.js"),["assets/index.4e8f928a.js","assets/index.9ffc0f45.css","assets/vendor.c5d1512f.js"])}],x=m({history:_(),routes:E});window.global=window;const c=h(b);c.config.globalProperties.eventBus=new v;c.use(x);c.mount("#app");
