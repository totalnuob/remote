

export class CommonTableau {

    public tableau_func() {
        //(function() {
        //    window.tableau = window.tableau || {},
        //        function(n) {
        //            function t() {
        //                for (var i, n, r = document.getElementsByTagName("script"), t = r.length - 1; t >= 0; t -= 1)
        //                    if (i = r[t], /viz_v1\.js/.test(i.src)) break;
        //                return n = new RegExp(".*?[^/:]/").exec(i.src), n && (n[0].toLowerCase().indexOf("http://") !== -1 || n[0].toLowerCase().indexOf("https://") !== -1) || (n = new RegExp(".*?[^/:]/").exec(window.location.href)), n ? n[0].toLowerCase() : ""
        //            }
        //            n._apiScripts = n._apiScripts || [];
        //            n._apiScripts.push(t())
        //        }(window.tableau);
        //    window.tableau._apiLoaded || (window.tableau._apiLoaded = !0, function() {
        //        function p() {
        //            return typeof pageXOffset != "undefined" ? window.pageXOffset : document.documentElement.scrollLeft
        //        }
        //
        //        function w() {
        //            return typeof pageYOffset != "undefined" ? window.pageYOffset : document.documentElement.scrollTop
        //        }
        //
        //        function u(n) {
        //            return n === undefined || n === null
        //        }
        //
        //        function t(n) {
        //            return n === undefined || n === null || n.length === 0
        //        }
        //
        //        function b() {
        //            return !u(window.postMessage)
        //        }
        //
        //        function k(n, t) {
        //            return n && n.nodeType === rt && n.tagName.toLowerCase() === t.toLowerCase()
        //        }
        //
        //        function d(n, t) {
        //            return n && (" " + n.className + " ").replace(/[\n\t\r]/g, " ").indexOf(" " + t + " ") > -1
        //        }
        //
        //        function ut(n, t, i) {
        //            var r = n ? n.parentNode : null;
        //            for (i = i || document.body; r;) {
        //                if (d(r, t)) return r;
        //                r = r === i ? null : r.parentNode
        //            }
        //            return r
        //        }
        //
        //        function ft(n) {
        //            var i, t, u, r;
        //            for (r = [
        //                [/&/g, "&amp;"],
        //                [/</g, "&lt;"],
        //                [/>/g, "&gt;"],
        //                [/"/g, "&quot;"],
        //                [/'/g, "&#39;"],
        //                [/\//g, "&#47;"]
        //            ], i = n || "", t = 0, u = r.length; t < u; t++) i = i.replace(r[t][0], r[t][1]);
        //            return i
        //        }
        //
        //        function h(n, t) {
        //            return window.getComputedStyle ? window.getComputedStyle(n, t) : n.currentStyle
        //        }
        //
        //        function e(n, i) {
        //            var r;
        //            return t(n) ? i : (r = n.match(/^(yes|y|true|t|1)$/i), !t(r))
        //        }
        //
        //        function et(n) {
        //            var i, r, u = [];
        //            if (t(n.filter)) return "";
        //            for (i = 0, r = n.filter.length; i < r; i++) u.push("&" + n.filter[i]);
        //            return u.join("")
        //        }
        //
        //        function ot(n) {
        //            return n = n.replace(/^\//, ""), n.charAt(n.length) !== "/" && (n += "/"), n
        //        }
        //
        //        function st(n, i, r) {
        //            var e, o, f, u = [i];
        //            f = r.site_root ? r.site_root : "";
        //            f.length > 0 && (f = ot(f));
        //            t(r.path) ? t(r.ticket) ? u.push(f + "views/" + n) : u.push("trusted/" + r.ticket + "/" + f + "views/" + n) : u.push(r.path);
        //            u.push("?:embed=y");
        //            u.push("&:showVizHome=no");
        //            o = {
        //                "load-order": 1,
        //                width: 1,
        //                height: 1,
        //                embed: 1,
        //                filter: 1,
        //                path: 1,
        //                ticket: 1,
        //                serverRoot: 1,
        //                static_image: 1,
        //                site_prefix: 1,
        //                site_root: 1,
        //                bootstrapWhenNotified: 1
        //            };
        //            for (e in r) r.hasOwnProperty(e) && o[e] === undefined && u.push("&:" + e + "=" + r[e]);
        //            return u.join("")
        //        }
        //
        //        function g(n, i) {
        //            var r, h = n.childNodes,
        //                s, c, u, f, o = "",
        //                l;
        //            for (i = i || {}, s = 0, c = h.length; s < c; s++) r = h[s], k(r, "param") && r.name ? (u = r.name, f = r.value ? r.value : null, u === "name" ? o = f : u === "filter" ? (i.filter = i.filter || [], i.filter.push(f)) : (i[u] = f, u === "mobile" && e(f, !1) && (y = !0))) : r.childNodes && r.childNodes.length > 0 && !k(r, "object") && (l = g(r, i), o = t(o) ? l : o);
        //            return o
        //        }
        //
        //        function nt(n) {
        //            var u, r, i, f;
        //            return i = t(n.host_url) ? t(n.serverRoot) ? tableau._apiScripts[0] : n.serverRoot : decodeURIComponent(n.host_url), u = new RegExp(".*//"), f = new RegExp("https?://[-A-Za-z0-9]*public[-A-Za-z0-9]*\\.tableau"), r = u.exec(window.location.href)[0], r.indexOf("file://") === -1 && i.indexOf(r) === -1 && i.match(f) !== null && (i = i.replace(u, r)), i
        //        }
        //
        //        function ht(n, t) {
        //            for (var i = 0; n[i] !== undefined; i += 1)
        //                if (n[i] === t) return !0;
        //            return !1
        //        }
        //
        //        function ct(n, t) {
        //            for (var i = 0; n[i] !== undefined; i += 1)
        //                if (n[i] === t) return n.splice(i, 1), !0;
        //            return !1
        //        }
        //
        //        function c(n) {
        //            var i = n.getBoundingClientRect(),
        //                t = h(n),
        //                r = parseInt(t.paddingLeft, 10) || 0,
        //                f = parseInt(t.paddingRight, 10) || 0,
        //                u = parseInt(t.paddingTop, 10) || 0,
        //                e = parseInt(t.paddingBottom, 10) || 0,
        //                o = parseInt(t.borderLeftWidth, 10) || 0,
        //                s = parseInt(t.borderTopWidth, 10) || 0;
        //            return {
        //                left: i.left + r + o + p(),
        //                top: i.top + u + s + w(),
        //                width: n.clientWidth - (r + f),
        //                height: n.clientHeight - (u + e)
        //            }
        //        }
        //
        //        function tt(n, t) {
        //            var i = Math.max(n.left, t.left),
        //                r = Math.max(n.top, t.top),
        //                u = Math.min(n.left + n.width, t.left + t.width),
        //                f = Math.min(n.top + n.height, t.top + t.height);
        //            return u <= i || f <= r ? {
        //                left: 0,
        //                top: 0,
        //                width: 0,
        //                height: 0
        //            } : {
        //                left: i,
        //                top: r,
        //                width: u - i,
        //                height: f - r
        //            }
        //        }
        //
        //        function lt(n) {
        //            for (var r, u, i = c(n), t = n.parentElement; t && t.parentElement; t = t.parentElement) r = h(t).overflow, (r === "auto" || r === "scroll" || r === "hidden") && (i = tt(i, c(t)));
        //            return u = c(document.documentElement), u.left += p(), u.top += w(), tt(i, u)
        //        }
        //
        //        function at(n, t) {
        //            var i = tableau.vizs[n]._loadOrder,
        //                r = tableau.vizs[t]._loadOrder;
        //            return i < r ? -1 : i === r ? 0 : 1
        //        }
        //
        //        function a(n) {
        //            var t = n > -1 ? tableau.vizs[i[n]] : null;
        //            t && t._onLoaded()
        //        }
        //
        //        function o(t) {
        //            var r, u;
        //            t === n && (u = n + 1, r = tableau.vizs[i[u]], r && (n = u, r.load(), window.postMessage && setTimeout(function() {
        //                o(n)
        //            }, 3e3)))
        //        }
        //
        //        function vt() {
        //            return document.baseURI !== document.location.href
        //        }
        //
        //        function yt() {
        //            var t;
        //            v ? (t = tableau.vizs[i[n]], t && t._iframe.readyState === "complete" && (a(n), o(n))) : (a(n), o(n))
        //        }
        //
        //        function it() {
        //            setTimeout(yt, 3e3)
        //        }
        //
        //        function s(t) {
        //            var u, r, s, f, e;
        //            if (t.data && (typeof t.data == "string" || t.data instanceof String))
        //                if (s = t.data.split(","), f = s[0], f === "tableau.completed" || f === "completed") {
        //                    if (e = parseInt(s[1], 10), isNaN(e))
        //                        for (u = 0; u <= n; u++) r = tableau.vizs[i[u]], r && r._hideLoadIndicators();
        //                    a(e);
        //                    o(e)
        //                } else if (f === "tableau.loadIndicatorsLoaded") r = tableau.vizs[i[n]], r && r._hideLoadIndicators();
        //                else if (f === "layoutInfoReq")
        //                    for (u = 0; u <= n; u++) r = tableau.vizs[i[u]], r && r._sendVisibleRect();
        //                else f === "tableau.listening" && (r = tableau.vizs[i[n]], r && r._enableVisibleRectCommunication())
        //        }
        //
        //        function pt() {
        //            var n = navigator.userAgent;
        //            return n.indexOf("iPad") !== -1 ? !0 : n.indexOf("Android") !== -1 ? !0 : n.indexOf("AppleWebKit") !== -1 && n.indexOf("Mobile") !== -1 ? !0 : !1
        //        }
        //
        //        function wt() {
        //            function t(n, t) {
        //                var i, r, u, f;
        //                i = 0;
        //                r = 0;
        //                u = document.documentElement.clientWidth / window.innerWidth;
        //                f = "sf," + t + "," + u + "," + i + "," + r;
        //                n.postMessage(f, "*")
        //            }
        //
        //            function i(i) {
        //                for (var u = i.data.split(","), r = 0, f; r < u.length;) n || u[r] !== "tableau.completed" && u[r] !== "completed" || (n = !0, t(i.source, -1)), u[r] === "sf?" && (f = u[++r], t(i.source, f)), ++r
        //            }
        //            var n = !1;
        //            return window.addEventListener && window.addEventListener("message", i, !1), this
        //        }
        //
        //        function r(n) {
        //            this._objectElement = n
        //        }
        //
        //        function bt() {
        //            for (var n, r = document.getElementsByTagName("object"), u = [], t = 0, i = r.length; t < i; t++) n = r[t], d(n, "tableauViz") && !ht(l, n) && (l.push(n), u.push(n));
        //            return u
        //        }
        //
        //        function kt(n) {
        //            for (var r = window.tableau._apiScripts, t = 0, i = Math.min(n.length, r.length); t < i; t++) n[t].params.serverRoot = r[t]
        //        }
        //
        //        function dt() {
        //            for (var n = document.getElementById("tableau_hide_this"); n;) n.parentNode.removeChild(n), n = document.getElementById("tableau_hide_this")
        //        }
        //
        //        function f() {
        //            var t, e, f, s, r, c, u = [];
        //            if (s = bt(), s.length !== 0) {
        //                for (dt(), t = 0, e = s.length; t < e; t++) f = s[t], r = {
        //                    filter: [],
        //                    ticket: "",
        //                    path: ""
        //                }, c = g(f, r), r.width = parseInt(h(f, null).width, 10), isNaN(r.width) && delete r.width, r.height = parseInt(h(f, null).height, 10), isNaN(r.height) && delete r.height, u.push({
        //                    name: c,
        //                    objectElement: f,
        //                    params: r
        //                });
        //                for (kt(u), t = 0, e = u.length; t < e; t++) tableau.createViz(u[t].name, u[t].objectElement, u[t].params);
        //                i.sort(at);
        //                o(n)
        //            }
        //        }
        //        var rt = 1,
        //            v = navigator.userAgent.indexOf("MSIE") > -1 && !window.opera,
        //            n = -1,
        //            y, l = [],
        //            i = [];
        //        r.prototype.getRootElement = function() {
        //            return this._ensurePlaceholderDiv() ? this._placeholderDiv : null
        //        };
        //        r.prototype.sizeToObjectElement = function(n) {
        //            var t = this._objectElement;
        //            u(t) || (t.width && (n.style.width = parseInt(t.width, 10).toString() === t.width ? t.width + "px" : t.width), t.height && (n.style.height = parseInt(t.height, 10).toString() === t.height ? t.height + "px" : t.height))
        //        };
        //        r.prototype.createLoadingFeedback = function(n, i) {
        //            var r, f, s, u, h, l = this,
        //                o, c = e(i.display_spinner, !0);
        //            if (o = vt(), c !== !0 || o || (i.display_spinner = "no"), this._ensurePlaceholderDiv()) return this.sizeToObjectElement(this._placeholderDiv), f = this._placeholderDiv.style, f.position = "relative", f.overflow = "hidden", f.display = "none", r = ['<div style="position:absolute;top:0;left:0;right:0;bottom:0;border:0;padding:0;margin:0">'], o || (r.push('<div style="top:20%;left:0;right:0;text-align:center;position:absolute;z-index:991;box-shadow:none">'), r.push('<style>#svg-spinner,#svg-spinner-container{position:absolute;top:0;bottom:0;left:0;right:0;margin:auto}#svg-spinner-container{width:65px;height:65px;border-radius:10px}#svg-spinner{width:50px;height:50px;-webkit-animation:svg-spinner-animation 1s linear infinite;animation:svg-spinner-animation 1s linear infinite}@-webkit-keyframes svg-spinner-animation{100%{-webkit-transform:rotate(360deg);}}@keyframes svg-spinner-animation{100%{transform:rotate(360deg)}}<\/style><div id="svg-spinner-container"><div id="svg-spinner"><svg viewBox="0 0 50 50"><g><path d="M42.7,7.3L40,10c3.8,3.8,6.2,9.1,6.2,15c0,11.7-9.5,21.1-21.1,21.1C13.3,46.1,3.9,36.7,3.9,25H0c0,13.8,11.2,25,25,25c13.8,0,25-11.2,25-25C50,18.1,47.2,11.9,42.7,7.3z" style="fill:#616570;"/><\/g><g><defs><path id="svg-spinner-circle" d="M25,50C11.2,50,0,38.8,0,25C0,11.2,11.2,0,25,0c13.8,0,25,11.2,25,25C50,38.8,38.8,50,25,50z M25,3.9C13.3,3.9,3.9,13.3,3.9,25c0,11.7,9.5,21.1,21.1,21.1S46.1,36.7,46.1,25C46.1,13.3,36.7,3.9,25,3.9z"/><\/defs><clipPath id="svg-spinner-clippath"><use xlink:href="#svg-spinner-circle"style="overflow:visible;"/><\/clipPath><linearGradient id="svg-spinner-gradient" gradientUnits="userSpaceOnUse" x1="0" y1="12.4998" x2="50"y2="12.4998"><stop offset="0" style="stop-color:#616570;stop-opacity:0"/><stop offset="0.15" style="stop-color:#616570;stop-opacity:0.04"/><stop offset="0.3" style="stop-color:#616570;stop-opacity:0.16"/><stop offset="0.45" style="stop-color:#616570;stop-opacity:0.36"/><stop offset="0.61" style="stop-color:#616570;stop-opacity:0.64"/><stop offset="0.76" style="stop-color:#616570"/><\/linearGradient><polygon points="25,25 0,25 0,0 50,0" style="clip-path:url(#svg-spinner-clippath);fill:url(#svg-spinner-gradient);"/><\/g><\/svg><\/div><\/div>'), r.push("<\/div>")), e(i.display_overlay, !0) && r.push("<div style='position:absolute;top:0;left:0;right:0;bottom:0;z-index:990;background-color:#EBEBEB;opacity:.24'><\/div>"), e(i.display_static_image, !0) && !t(i.static_image) && (r.push("<style>#svg-spinner-container{border-radius:12px;background:rgba(255,255,255,.6);<\/style>"), i.display_static_image = "no", s = e(i.tabs, !1), h = s ? "31px;" : "9px", r.push('<div style="position: absolute; top: 0; left: 0; right: 0; bottom: 0; '), r.push("background: transparent url('"), r.push(ft(i.static_image)), r.push("') no-repeat scroll 0 0; box-shadow: none; left: 8px; top:"), r.push(h), r.push('"><\/div>')), r.push("<\/div>"), u = document.createElement("div"), u.innerHTML = r.join(""), this._glassPaneElement = u.firstChild, this._placeholderDiv.appendChild(this._glassPaneElement), u.innerHTML = "", u = null, this._createAndAppendIframe()
        //        };
        //        r.prototype.show = function() {
        //            this._placeholderDiv && (this._placeholderDiv.style.display = "block")
        //        };
        //        r.prototype.dispose = function() {
        //            if (this._glassPaneElement) {
        //                var n, t, i;
        //                n = 150;
        //                this._glassPaneElement.style.transition = n + "ms opacity";
        //                this._glassPaneElement.style.opacity = 0;
        //                t = function(n) {
        //                    n.innerHTML = "";
        //                    n.parentNode.removeChild(n);
        //                    n = null
        //                };
        //                i = this._glassPaneElement;
        //                window.setTimeout(function() {
        //                    t(i)
        //                }, n)
        //            }
        //            this._objectElement && (ct(l, this._objectElement), this._objectElement.parentNode.removeChild(this._objectElement), this._objectElement = null)
        //        };
        //        r.prototype._ensurePlaceholderDiv = function() {
        //            var n;
        //            return u(this._placeholderDiv) ? u(this._objectElement) ? !1 : (n = ut(this._objectElement, "tableauPlaceholder"), n || (n = document.createElement("div"), n.className = "tableauPlaceholder", this._objectElement.parentNode.insertBefore(n, this._objectElement), n.appendChild(this._objectElement), this._objectElement.style.display = "none"), this._placeholderDiv = n, !0) : !0
        //        };
        //        r.prototype._createAndAppendIframe = function() {
        //            var i, t, f, n, e, o, s, r;
        //            if (!u(this._objectElement)) {
        //                for (i = this._objectElement.attributes, n = document.createElement("iframe"), n.frameBorder = "0", n.marginHeight = "0", n.marginWidth = "0", n.setAttribute("allowTransparency", "true"), t = 0, f = i.length; t < f; t++) i[t].specified && n.setAttribute(i[t].name, i[t].value);
        //                return n.style.cssText = this._objectElement.style.cssText, n.style.margin = "0px", n.style.padding = "0px", n.style.border = "none", this.sizeToObjectElement(n), r = navigator.userAgent, o = r.indexOf("WebKit") >= 0, e = r.indexOf("Chrome") >= 0, s = r.indexOf("Safari") >= 0 || o && !e, s && n.addEventListener("mousewheel", function() {}), this._ensurePlaceholderDiv() && this._placeholderDiv.appendChild(n), n
        //            }
        //        };
        //        window.tableau.Viz = function(n, t, i) {
        //            this._name = n;
        //            this._iframe = t;
        //            this._filterArgs = "";
        //            this._filterOpts = et(i);
        //            this._serverRoot = nt(i);
        //            this._baseUrl = st(this._name, this._serverRoot, i);
        //            this._loadOrder = i["load-order"] ? parseInt(i["load-order"], 10) : 0;
        //            this._filter = {}
        //        };
        //        window.tableau.vizs = [];
        //        window.tableau.createViz = function(n, u, f) {
        //            var o, s, h, e;
        //            h = nt(f);
        //            s = new r(u);
        //            o = s.createLoadingFeedback(h, f);
        //            s.show();
        //            e = new tableau.Viz(n, o, f);
        //            e._loadFeedback = s;
        //            e.show();
        //            i.push(this.vizs.length);
        //            this.vizs.push(e);
        //            t(n) || (this.vizs[n] = e);
        //            window.postMessage || (v ? o.onreadystatechange = it : o.onload = it)
        //        };
        //        window.tableau.Viz.prototype.load = function() {
        //            var t = "&:loadOrderID=" + n,
        //                i = window.top === window.self ? "" : "&:increment_view_count=no";
        //            this._iframe.src = this._baseUrl + this._filterOpts + t + i
        //        };
        //        window.tableau.Viz.prototype.show = function() {
        //            this._iframe.style.display = "block"
        //        };
        //        window.tableau.Viz.prototype.hide = function() {
        //            this._iframe.style.display = "none"
        //        };
        //        window.tableau.Viz.prototype.refresh = function() {
        //            this._iframe.src = this._baseUrl + this._filterOpts + this._filterArgs + "&:refresh=true"
        //        };
        //        window.tableau.Viz.prototype.revert = function() {
        //            this._iframe.src = this._baseUrl + "&:revert=all";
        //            this._filterArgs = "";
        //            this._filter = {}
        //        };
        //        window.tableau.Viz.prototype.filter = function(n) {
        //            var t;
        //            if (u(n)) {
        //                this.revert();
        //                return
        //            }
        //            for (t in n) this._filter[t] = n[t];
        //            this._filterArgs = "";
        //            for (t in n) this._filterArgs += "&" + encodeURIComponent(t) + "=", this._filterArgs += typeof n[t] == "string" ? encodeURIComponent(n[t]) : encodeURIComponent(n[t].join(","));
        //            this._iframe.src = this._baseUrl + this._filterOpts + this._filterArgs
        //        };
        //        window.tableau.Viz.prototype._sendVisibleRect = function() {
        //            var n, t, i;
        //            b() && this._iframe && this._iframe.contentWindow && (n = lt(this._iframe), t = c(this._iframe), i = ["layoutInfoResp", n.left - t.left, n.top - t.top, n.width, n.height].join(","), this._iframe.contentWindow.postMessage(i, "*"))
        //        };
        //        window.tableau.Viz.prototype._enableVisibleRectCommunication = function() {
        //            b() && this._iframe && this._iframe.contentWindow && this._iframe.contentWindow.postMessage("tableau.enableVisibleRectCommunication", "*")
        //        };
        //        window.tableau.Viz.prototype._onLoaded = function() {
        //            this._hideLoadIndicators();
        //            this._enableVisibleRectCommunication()
        //        };
        //        window.tableau.Viz.prototype._hideLoadIndicators = function() {
        //            this._loadFeedback && (this._loadFeedback.dispose(), this._loadFeedback = null, delete this._loadFeedback)
        //        };
        //        document.addEventListener ? (document.addEventListener("DOMContentLoaded", f, !1), document.addEventListener("message", s, !1), window.addEventListener("load", f, !1), window.addEventListener("message", s, !1)) : document.attachEvent ? (document.attachEvent("onreadystatechange", f), document.attachEvent("onmessage", s), window.attachEvent("onload", f), window.attachEvent("onmessage", s)) : (window.onload = f, window.onmessage = s);
        //        (pt() || y) && wt();
        //        tableau._createNewVizesAndStartLoading = f
        //    }());
        //    document.readyState === "complete" && tableau._createNewVizesAndStartLoading()
        //})();
    }
}