/*******************************************************************************
 * Copyright 2015 Unicon (R) Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *******************************************************************************/
/**
 * angular-ui-notification - Angular.js service providing simple notifications using Bootstrap 3 styles with css transitions for animating
 * @author Alex_Crack
 * @version v0.0.11
 * @link https://github.com/alexcrack/angular-ui-notification
 * @license MIT
 */
angular.module("ui-notification",[]),angular.module("ui-notification").value("uiNotificationTemplates","angular-ui-notification.html"),angular.module("ui-notification").provider("Notification",function(){this.options={delay:5e3,startTop:10,startRight:10,verticalSpacing:10,horizontalSpacing:10,positionX:"right",positionY:"top",replaceMessage:!1},this.setOptions=function(t){if(!angular.isObject(t))throw new Error("Options should be an object!");this.options=angular.extend({},this.options,t)},this.$get=["$timeout","uiNotificationTemplates","$http","$compile","$templateCache","$rootScope","$injector","$sce","$q",function(t,e,i,n,o,a,s,r,l){var p=this.options,c=p.startTop,u=p.startRight,d=p.verticalSpacing,m=p.horizontalSpacing,g=p.delay,f=[],h=function(s,h){var v=l.defer();return"object"!=typeof s&&(s={message:s}),s.scope=s.scope?s.scope:a,s.template=s.template?s.template:e,s.delay=angular.isUndefined(s.delay)?g:s.delay,s.type=h?h:"",s.positionY=s.positionY?s.positionY:p.positionY,s.positionX=s.positionX?s.positionX:p.positionX,s.replaceMessage=s.replaceMessage?s.replaceMessage:p.replaceMessage,i.get(s.template,{cache:o}).success(function(e){var i=s.scope.$new();i.message=r.trustAsHtml(s.message),i.title=r.trustAsHtml(s.title),i.t=s.type.substr(0,1),i.delay=s.delay;var o=function(){for(var t=0,e=0,i=c,n=u,o=[],a=f.length-1;a>=0;a--){var r=f[a];if(s.replaceMessage&&a<f.length-1)r.addClass("killed");else{var l=parseInt(r[0].offsetHeight),p=parseInt(r[0].offsetWidth),g=o[r._positionY+r._positionX];h+l>window.innerHeight&&(g=c,e++,t=0);var h=(i=g?g:c)+(0===t?0:d),v=n+e*(m+p);r.css(r._positionY,h+"px"),r.css(r._positionX,v+"px"),o[r._positionY+r._positionX]=h+l,t++}}},a=n(e)(i);a._positionY=s.positionY,a._positionX=s.positionX,a.addClass(s.type),a.bind("webkitTransitionEnd oTransitionEnd otransitionend transitionend msTransitionEnd click",function(t){t=t.originalEvent||t,("click"===t.type||"opacity"===t.propertyName&&t.elapsedTime>=1)&&(a.remove(),f.splice(f.indexOf(a),1),o())}),angular.isNumber(s.delay)&&t(function(){a.addClass("killed")},s.delay),angular.element(document.getElementsByTagName("body")).append(a);var l=-(parseInt(a[0].offsetHeight)+50);a.css(a._positionY,l+"px"),f.push(a),i._templateElement=a,i.kill=function(e){e?(f.splice(f.indexOf(i._templateElement),1),i._templateElement.remove(),t(o)):i._templateElement.addClass("killed")},t(o),v.resolve(i)}).error(function(t){throw new Error("Template ("+s.template+") could not be loaded. "+t)}),v.promise};return h.primary=function(t){return this(t,"primary")},h.error=function(t){return this(t,"error")},h.success=function(t){return this(t,"success")},h.info=function(t){return this(t,"info")},h.warning=function(t){return this(t,"warning")},h.clearAll=function(){var t=angular.element(document.getElementsByClassName("ui-notification"));t&&angular.forEach(t,function(t){t.remove()})},h}]}),angular.module("ui-notification").run(["$templateCache",function(t){t.put("angular-ui-notification.html",'<div class="ui-notification"><h3 ng-show="title" ng-bind-html="title"></h3><div class="message" ng-bind-html="message"></div></div>')}]);