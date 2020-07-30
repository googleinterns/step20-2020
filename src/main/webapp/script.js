// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// Set the sign-in link for the sign-in page.
function getSignInLink() {
  fetch('/sign-in').then(response => response.json()).then(info => {
    const linkEl = document.getElementById('sign-in-button');
    linkEl.href = info.url;
  });
}

// Show sign-in fail message if appropriate.
function signInFailMessage() {
  var url = window.location.href;
  var status = url.split('=')[1];

  if(status === 'fail') {
    document.getElementById('fail-message').classList.remove('d-none');
    document.getElementById('normal-title').classList.add('d-none');
    document.getElementById('normal-message').classList.add('d-none');
  }
}

// Set the sign-up link for the sign-up page.
function getSignUpLink() {
  fetch('/sign-up').then(response => response.text()).then(link => {
    const linkEl = document.getElementById('sign-up-button');
    linkEl.href = link;
  });
}

// Get a specific user's data to populate their profile page.
function getProfilePageData() {
  var url = window.location.href;
  var key = url.split('?')[1];

  fetch('/user?' + key).then(response => response.json()).then(userInfo => {
    document.getElementById('profile-pic-display').src = '/blob?blob-key=' +  userInfo.profilePicKey;
    document.getElementById('username-display').innerHTML = userInfo.username;
    document.getElementById('location-display').innerHTML = userInfo.location;
    document.getElementById('bio-display').innerHTML = userInfo.bio;

    document.getElementById('username').innerHTML = userInfo.username;
    document.getElementById('location').innerHTML = userInfo.location;
    document.getElementById('bio').innerHTML = userInfo.bio;

    if(!userInfo.isCurrentUser) {
      document.getElementById('edit-button').classList.add('d-none');
    }
  });
}

// Sets the image upload URL in the account creation and profile pages.
function fetchBlobstoreUrl() {
  fetch('/profile-pic-upload-url').then(response => response.text()).then(imageUploadUrl => {
    const signupForm = document.getElementById('user-form');
    signupForm.action = imageUploadUrl;
  });
}

// Enables or disables the editable form in the profile page.
function toggleProfileEditMode() {
  const staticEl = document.getElementById('static-user-info');
  const editableEl = document.getElementById('user-form');
  const buttonEl = document.getElementById('edit-button');

  if(staticEl.classList.contains('d-none')) {
    staticEl.classList.remove('d-none');
    editableEl.classList.add('d-none');
    buttonEl.innerHTML = 'Edit Profile';
  }
  else {
    staticEl.classList.add('d-none');
    editableEl.classList.remove('d-none');
    buttonEl.innerHTML = 'Back';
  }
}

/** Fetches comments from the server and adds them to the DOM. */
function loadComments() {
  fetch('/display-comments').then(response => response.json()).then((comments) => {
    const commentListElement = document.getElementById('comment-list');
    comments.forEach((comment) => {
      commentListElement.appendChild(createCommentElement(comment));
    })
  });
}

/** Creates a list element that represents a comment. */
function createCommentElement(comment) {
  const commentElement = document.createElement('div');
  commentElement.className = 'small-sep';

  const userComment = document.createElement('span');
  var userInfoDisplayed = comment.username + " • " + comment.location + " • " + comment.MMDDYYYY;
  userComment.innerHTML += addParagraph(userInfoDisplayed) + addParagraph(comment.comment);

  commentElement.appendChild(spanElement);
  return commentElement;
}

/** Fetches results from the server and adds them to the DOM. */
function getResults(param) {
  var userQuery = getURLParamVal(param);
  // Search string is in all caps, so the userQuery should also be in all caps for querying purposes.
  fetch('/results?user-query=' + userQuery.toUpperCase()).then(response => response.json()).then((results) => {
    const resultListElement = document.getElementById('result-list');
    results.forEach((result) => {
      resultListElement.appendChild(createResultElement(result));
    })
  });
}

/** Fetches live streams from the server and adds them to the DOM. */
function loadLiveStreams() {
  fetch('/display-livestreams').then(response => response.json()).then((livestreams) => {
    const liveStreamListElement = document.getElementById('livestream-list');
    livestreams.forEach((livestream) => {
      liveStreamListElement.appendChild(createLiveStreamElement(livestream));
    })
  });
}

/** Creates a list element that represents a live stream. */
function createLiveStreamElement(liveStream) {
  const liveStreamElement = document.createElement('li');
  liveStreamElement.className = 'live-stream';

  const spanElement = document.createElement('span');
  spanElement.innerText = liveStream.liveStreamKey;

  liveStreamElement.appendChild(spanElement);
  return liveStreamElement;
}

/** Gets the ID of a YouTube video from its URL.
    Will work only for a URL of a certain format
    (i.e. youtube.com/watch?v=). */
function getIdFromUrl(url) {
  return url.substring(url.lastIndexOf('=') + 1);
}

/** Gets the ID of the YouTube video that the user inputs. */
function getId() {
  const liveStreamLink = document.getElementById('live-stream-link').value;
  return getIdFromUrl(liveStreamLink);
}

/** Gets the ID of the YouTube video that the user inputs. */
function storeLiveStreamInfo(schedStartTime, schedEndTime, duration) {
  const recipeSelection = document.getElementById('recipe-selection');
  const recipeKey = recipeSelection.options[recipeSelection.selectedIndex].text;
  const liveStreamLink = document.getElementById('live-stream-link').value;
  fetch('/new-live-stream?recipe-key=' + recipeKey + '&live-stream-link=' + liveStreamLink + '&sched-start-time=' + schedStartTime + '&sched-end-time=' + schedEndTime + '&duration=' + duration);
}

/** Videos: List JSON Response Retrieval */
// https://apis.google.com/js/api.js
var gapi = window.gapi = window.gapi || {};
gapi._bs = new Date().getTime();
(function() {
    /*
     Copyright The Closure Library Authors.
     SPDX-License-Identifier: Apache-2.0
    */
    var g = this || self,
        h = function(a) {
            return a
        };
    var m = function() {
        this.g = ""
    };
    m.prototype.toString = function() {
        return "SafeStyle{" + this.g + "}"
    };
    m.prototype.a = function(a) {
        this.g = a
    };
    (new m).a("");
    var n = function() {
        this.f = ""
    };
    n.prototype.toString = function() {
        return "SafeStyleSheet{" + this.f + "}"
    };
    n.prototype.a = function(a) {
        this.f = a
    };
    (new n).a("");
    /* gapi.loader.OBJECT_CREATE_TEST_OVERRIDE && */
    var q = window,
        v = document,
        aa = q.location,
        ba = function() {},
        ca = /\[native code\]/,
        x = function(a, b, c) {
            return a[b] = a[b] || c
        },
        da = function(a) {
            a = a.sort();
            for (var b = [], c = void 0, d = 0; d < a.length; d++) {
                var e = a[d];
                e != c && b.push(e);
                c = e
            }
            return b
        },
        C = function() {
            var a;
            if ((a = Object.create) && ca.test(a)) a = a(null);
            else {
                a = {};
                for (var b in a) a[b] = void 0
            }
            return a
        },
        D = x(q, "gapi", {});
    var E;
    E = x(q, "___jsl", C());
    x(E, "I", 0);
    x(E, "hel", 10);
    var F = function() {
            var a = aa.href;
            if (E.dpo) var b = E.h;
            else {
                b = E.h;
                var c = /([#].*&|[#])jsh=([^&#]*)/g,
                    d = /([?#].*&|[?#])jsh=([^&#]*)/g;
                if (a = a && (c.exec(a) || d.exec(a))) try {
                    b = decodeURIComponent(a[2])
                } catch (e) {}
            }
            return b
        },
        ea = function(a) {
            var b = x(E, "PQ", []);
            E.PQ = [];
            var c = b.length;
            if (0 === c) a();
            else
                for (var d = 0, e = function() {
                        ++d === c && a()
                    }, f = 0; f < c; f++) b[f](e)
        },
        G = function(a) {
            return x(x(E, "H", C()), a, C())
        };
    var H = x(E, "perf", C()),
        K = x(H, "g", C()),
        fa = x(H, "i", C());
    x(H, "r", []);
    C();
    C();
    var L = function(a, b, c) {
            var d = H.r;
            "function" === typeof d ? d(a, b, c) : d.push([a, b, c])
        },
        N = function(a, b, c) {
            b && 0 < b.length && (b = M(b), c && 0 < c.length && (b += "___" + M(c)), 28 < b.length && (b = b.substr(0, 28) + (b.length - 28)), c = b, b = x(fa, "_p", C()), x(b, c, C())[a] = (new Date).getTime(), L(a, "_p", c))
        },
        M = function(a) {
            return a.join("__").replace(/\./g, "_").replace(/\-/g, "_").replace(/,/g, "_")
        };
    var O = C(),
        P = [],
        S = function(a) {
            throw Error("Bad hint" + (a ? ": " + a : ""));
        };
    P.push(["jsl", function(a) {
        for (var b in a)
            if (Object.prototype.hasOwnProperty.call(a, b)) {
                var c = a[b];
                "object" == typeof c ? E[b] = x(E, b, []).concat(c) : x(E, b, c)
            } if (b = a.u) a = x(E, "us", []), a.push(b), (b = /^https:(.*)$/.exec(b)) && a.push("http:" + b[1])
    }]);
    var ia = /^(\/[a-zA-Z0-9_\-]+)+$/,
        T = [/\/amp\//, /\/amp$/, /^\/amp$/],
        ja = /^[a-zA-Z0-9\-_\.,!]+$/,
        ka = /^gapi\.loaded_[0-9]+$/,
        la = /^[a-zA-Z0-9,._-]+$/,
        pa = function(a, b, c, d) {
            var e = a.split(";"),
                f = e.shift(),
                l = O[f],
                k = null;
            l ? k = l(e, b, c, d) : S("no hint processor for: " + f);
            k || S("failed to generate load url");
            b = k;
            c = b.match(ma);
            (d = b.match(na)) && 1 === d.length && oa.test(b) && c && 1 === c.length || S("failed sanity: " + a);
            return k
        },
        ra = function(a, b, c, d) {
            a = qa(a);
            ka.test(c) || S("invalid_callback");
            b = U(b);
            d = d && d.length ? U(d) : null;
            var e =
                function(f) {
                    return encodeURIComponent(f).replace(/%2C/g, ",")
                };
            return [encodeURIComponent(a.pathPrefix).replace(/%2C/g, ",").replace(/%2F/g, "/"), "/k=", e(a.version), "/m=", e(b), d ? "/exm=" + e(d) : "", "/rt=j/sv=1/d=1/ed=1", a.b ? "/am=" + e(a.b) : "", a.i ? "/rs=" + e(a.i) : "", a.j ? "/t=" + e(a.j) : "", "/cb=", e(c)].join("")
        },
        qa = function(a) {
            "/" !== a.charAt(0) && S("relative path");
            for (var b = a.substring(1).split("/"), c = []; b.length;) {
                a = b.shift();
                if (!a.length || 0 == a.indexOf(".")) S("empty/relative directory");
                else if (0 < a.indexOf("=")) {
                    b.unshift(a);
                    break
                }
                c.push(a)
            }
            a = {};
            for (var d = 0, e = b.length; d < e; ++d) {
                var f = b[d].split("="),
                    l = decodeURIComponent(f[0]),
                    k = decodeURIComponent(f[1]);
                2 == f.length && l && k && (a[l] = a[l] || k)
            }
            b = "/" + c.join("/");
            ia.test(b) || S("invalid_prefix");
            c = 0;
            for (d = T.length; c < d; ++c) T[c].test(b) && S("invalid_prefix");
            c = V(a, "k", !0);
            d = V(a, "am");
            e = V(a, "rs");
            a = V(a, "t");
            return {
                pathPrefix: b,
                version: c,
                b: d,
                i: e,
                j: a
            }
        },
        U = function(a) {
            for (var b = [], c = 0, d = a.length; c < d; ++c) {
                var e = a[c].replace(/\./g, "_").replace(/-/g, "_");
                la.test(e) && b.push(e)
            }
            return b.join(",")
        },
        V = function(a, b, c) {
            a = a[b];
            !a && c && S("missing: " + b);
            if (a) {
                if (ja.test(a)) return a;
                S("invalid: " + b)
            }
            return null
        },
        oa = /^https?:\/\/[a-z0-9_.-]+\.google(rs)?\.com(:\d+)?\/[a-zA-Z0-9_.,!=\-\/]+$/,
        na = /\/cb=/g,
        ma = /\/\//g,
        sa = function() {
            var a = F();
            if (!a) throw Error("Bad hint");
            return a
        };
    O.m = function(a, b, c, d) {
        (a = a[0]) || S("missing_hint");
        return "https://apis.google.com" + ra(a, b, c, d)
    };
    var W = decodeURI("%73cript"),
        X = /^[-+_0-9\/A-Za-z]+={0,2}$/,
        ta = function(a, b) {
            for (var c = [], d = 0; d < a.length; ++d) {
                var e = a[d],
                    f;
                if (f = e) {
                    a: {
                        for (f = 0; f < b.length; f++)
                            if (b[f] === e) break a;f = -1
                    }
                    f = 0 > f
                }
                f && c.push(e)
            }
            return c
        },
        ua = function() {
            var a = E.nonce;
            return void 0 !== a ? a && a === String(a) && a.match(X) ? a : E.nonce = null : v.querySelector ? (a = v.querySelector("script[nonce]")) ? (a = a.nonce || a.getAttribute("nonce") || "", a && a === String(a) && a.match(X) ? E.nonce = a : E.nonce = null) : null : null
        },
        wa = function(a) {
            if ("loading" != v.readyState) va(a);
            else {
                var b = ua(),
                    c = "";
                null !== b && (c = ' nonce="' + b + '"');
                a = "<" + W + ' src="' + encodeURI(a) + '"' + c + "></" + W + ">";
                v.write(Y ? Y.createHTML(a) : a)
            }
        },
        va = function(a) {
            var b = v.createElement(W);
            b.setAttribute("src", Y ? Y.createScriptURL(a) : a);
            a = ua();
            null !== a && b.setAttribute("nonce", a);
            b.async = "true";
            (a = v.getElementsByTagName(W)[0]) ? a.parentNode.insertBefore(b, a): (v.head || v.body || v.documentElement).appendChild(b)
        },
        xa = function(a, b) {
            var c = b && b._c;
            if (c)
                for (var d = 0; d < P.length; d++) {
                    var e = P[d][0],
                        f = P[d][1];
                    f && Object.prototype.hasOwnProperty.call(c,
                        e) && f(c[e], a, b)
                }
        },
        za = function(a, b, c) {
            ya(function() {
                var d = b === F() ? x(D, "_", C()) : C();
                d = x(G(b), "_", d);
                a(d)
            }, c)
        },
        Ba = function(a, b) {
            var c = b || {};
            "function" == typeof b && (c = {}, c.callback = b);
            xa(a, c);
            b = a ? a.split(":") : [];
            var d = c.h || sa(),
                e = x(E, "ah", C());
            if (e["::"] && b.length) {
                a = [];
                for (var f = null; f = b.shift();) {
                    var l = f.split(".");
                    l = e[f] || e[l[1] && "ns:" + l[0] || ""] || d;
                    var k = a.length && a[a.length - 1] || null,
                        w = k;
                    k && k.hint == l || (w = {
                        hint: l,
                        c: []
                    }, a.push(w));
                    w.c.push(f)
                }
                var y = a.length;
                if (1 < y) {
                    var z = c.callback;
                    z && (c.callback = function() {
                        0 ==
                            --y && z()
                    })
                }
                for (; b = a.shift();) Aa(b.c, c, b.hint)
            } else Aa(b || [], c, d)
        },
        Aa = function(a, b, c) {
            a = da(a) || [];
            var d = b.callback,
                e = b.config,
                f = b.timeout,
                l = b.ontimeout,
                k = b.onerror,
                w = void 0;
            "function" == typeof k && (w = k);
            var y = null,
                z = !1;
            if (f && !l || !f && l) throw "Timeout requires both the timeout parameter and ontimeout parameter to be set";
            k = x(G(c), "r", []).sort();
            var Q = x(G(c), "L", []).sort(),
                I = [].concat(k),
                ha = function(u, A) {
                    if (z) return 0;
                    q.clearTimeout(y);
                    Q.push.apply(Q, p);
                    var B = ((D || {}).config || {}).update;
                    B ? B(e) : e && x(E, "cu",
                        []).push(e);
                    if (A) {
                        N("me0", u, I);
                        try {
                            za(A, c, w)
                        } finally {
                            N("me1", u, I)
                        }
                    }
                    return 1
                };
            0 < f && (y = q.setTimeout(function() {
                z = !0;
                l()
            }, f));
            var p = ta(a, Q);
            if (p.length) {
                p = ta(a, k);
                var r = x(E, "CP", []),
                    t = r.length;
                r[t] = function(u) {
                    if (!u) return 0;
                    N("ml1", p, I);
                    var A = function(J) {
                            r[t] = null;
                            ha(p, u) && ea(function() {
                                d && d();
                                J()
                            })
                        },
                        B = function() {
                            var J = r[t + 1];
                            J && J()
                        };
                    0 < t && r[t - 1] ? r[t] = function() {
                        A(B)
                    } : A(B)
                };
                if (p.length) {
                    var R = "loaded_" + E.I++;
                    D[R] = function(u) {
                        r[t](u);
                        D[R] = null
                    };
                    a = pa(c, p, "gapi." + R, k);
                    k.push.apply(k, p);
                    N("ml0", p, I);
                    b.sync ||
                        q.___gapisync ? wa(a) : va(a)
                } else r[t](ba)
            } else ha(p) && d && d()
        },
        Ca;
    var Da = null,
        Z = g.trustedTypes;
    if (Z && Z.createPolicy) try {
        Da = Z.createPolicy("gapi#gapi", {
            createHTML: h,
            createScript: h,
            createScriptURL: h
        })
    } catch (a) {
        g.console && g.console.error(a.message)
    }
    Ca = Da;
    var Y = Ca;
    var ya = function(a, b) {
        if (E.hee && 0 < E.hel) try {
            return a()
        } catch (c) {
            b && b(c), E.hel--, Ba("debug_error", function() {
                try {
                    window.___jsl.hefn(c)
                } catch (d) {
                    throw c;
                }
            })
        } else try {
            return a()
        } catch (c) {
            throw b && b(c), c;
        }
    };
    D.load = function(a, b) {
        return ya(function() {
            return Ba(a, b)
        })
    };
    K.bs0 = window.gapi._bs || (new Date).getTime();
    L("bs0");
    K.bs1 = (new Date).getTime();
    L("bs1");
    delete window.gapi._bs;
}).call(this);
gapi.load("", {
    callback: window["gapi_onload"],
    _c: {
        "jsl": {
            "ci": {
                "deviceType": "desktop",
                "oauth-flow": {
                    "authUrl": "https://accounts.google.com/o/oauth2/auth",
                    "proxyUrl": "https://accounts.google.com/o/oauth2/postmessageRelay",
                    "disableOpt": true,
                    "idpIframeUrl": "https://accounts.google.com/o/oauth2/iframe",
                    "usegapi": false
                },
                "debug": {
                    "reportExceptionRate": 0.05,
                    "forceIm": false,
                    "rethrowException": false,
                    "host": "https://apis.google.com"
                },
                "enableMultilogin": true,
                "googleapis.config": {
                    "auth": {
                        "useFirstPartyAuthV2": true
                    }
                },
                "isPlusUser": false,
                "inline": {
                    "css": 1
                },
                "disableRealtimeCallback": false,
                "drive_share": {
                    "skipInitCommand": true
                },
                "csi": {
                    "rate": 0.01
                },
                "client": {
                    "cors": false
                },
                "isLoggedIn": true,
                "signInDeprecation": {
                    "rate": 0.0
                },
                "include_granted_scopes": true,
                "llang": "en",
                "iframes": {
                    "youtube": {
                        "params": {
                            "location": ["search", "hash"]
                        },
                        "url": ":socialhost:/:session_prefix:_/widget/render/youtube?usegapi\u003d1",
                        "methods": ["scroll", "openwindow"]
                    },
                    "ytsubscribe": {
                        "url": "https://www.youtube.com/subscribe_embed?usegapi\u003d1"
                    },
                    "plus_circle": {
                        "params": {
                            "url": ""
                        },
                        "url": ":socialhost:/:session_prefix::se:_/widget/plus/circle?usegapi\u003d1"
                    },
                    "plus_share": {
                        "params": {
                            "url": ""
                        },
                        "url": ":socialhost:/:session_prefix::se:_/+1/sharebutton?plusShare\u003dtrue\u0026usegapi\u003d1"
                    },
                    "rbr_s": {
                        "params": {
                            "url": ""
                        },
                        "url": ":socialhost:/:session_prefix::se:_/widget/render/recobarsimplescroller"
                    },
                    ":source:": "3p",
                    "playemm": {
                        "url": "https://play.google.com/work/embedded/search?usegapi\u003d1\u0026usegapi\u003d1"
                    },
                    "savetoandroidpay": {
                        "url": "https://pay.google.com/gp/v/widget/save"
                    },
                    "blogger": {
                        "params": {
                            "location": ["search", "hash"]
                        },
                        "url": ":socialhost:/:session_prefix:_/widget/render/blogger?usegapi\u003d1",
                        "methods": ["scroll", "openwindow"]
                    },
                    "evwidget": {
                        "params": {
                            "url": ""
                        },
                        "url": ":socialhost:/:session_prefix:_/events/widget?usegapi\u003d1"
                    },
                    "partnersbadge": {
                        "url": "https://www.gstatic.com/partners/badge/templates/badge.html?usegapi\u003d1"
                    },
                    "dataconnector": {
                        "url": "https://dataconnector.corp.google.com/:session_prefix:ui/widgetview?usegapi\u003d1"
                    },
                    "surveyoptin": {
                        "url": "https://www.google.com/shopping/customerreviews/optin?usegapi\u003d1"
                    },
                    ":socialhost:": "https://apis.google.com",
                    "shortlists": {
                        "url": ""
                    },
                    "hangout": {
                        "url": "https://talkgadget.google.com/:session_prefix:talkgadget/_/widget"
                    },
                    "plus_followers": {
                        "params": {
                            "url": ""
                        },
                        "url": ":socialhost:/_/im/_/widget/render/plus/followers?usegapi\u003d1"
                    },
                    "post": {
                        "params": {
                            "url": ""
                        },
                        "url": ":socialhost:/:session_prefix::im_prefix:_/widget/render/post?usegapi\u003d1"
                    },
                    ":gplus_url:": "https://plus.google.com",
                    "signin": {
                        "params": {
                            "url": ""
                        },
                        "url": ":socialhost:/:session_prefix:_/widget/render/signin?usegapi\u003d1",
                        "methods": ["onauth"]
                    },
                    "rbr_i": {
                        "params": {
                            "url": ""
                        },
                        "url": ":socialhost:/:session_prefix::se:_/widget/render/recobarinvitation"
                    },
                    "share": {
                        "url": ":socialhost:/:session_prefix::im_prefix:_/widget/render/share?usegapi\u003d1"
                    },
                    "plusone": {
                        "params": {
                            "count": "",
                            "size": "",
                            "url": ""
                        },
                        "url": ":socialhost:/:session_prefix::se:_/+1/fastbutton?usegapi\u003d1"
                    },
                    "comments": {
                        "params": {
                            "location": ["search", "hash"]
                        },
                        "url": ":socialhost:/:session_prefix:_/widget/render/comments?usegapi\u003d1",
                        "methods": ["scroll", "openwindow"]
                    },
                    ":im_socialhost:": "https://plus.googleapis.com",
                    "backdrop": {
                        "url": "https://clients3.google.com/cast/chromecast/home/widget/backdrop?usegapi\u003d1"
                    },
                    "visibility": {
                        "params": {
                            "url": ""
                        },
                        "url": ":socialhost:/:session_prefix:_/widget/render/visibility?usegapi\u003d1"
                    },
                    "autocomplete": {
                        "params": {
                            "url": ""
                        },
                        "url": ":socialhost:/:session_prefix:_/widget/render/autocomplete"
                    },
                    "additnow": {
                        "url": "https://apis.google.com/marketplace/button?usegapi\u003d1",
                        "methods": ["launchurl"]
                    },
                    ":signuphost:": "https://plus.google.com",
                    "ratingbadge": {
                        "url": "https://www.google.com/shopping/customerreviews/badge?usegapi\u003d1"
                    },
                    "appcirclepicker": {
                        "url": ":socialhost:/:session_prefix:_/widget/render/appcirclepicker"
                    },
                    "follow": {
                        "url": ":socialhost:/:session_prefix:_/widget/render/follow?usegapi\u003d1"
                    },
                    "community": {
                        "url": ":ctx_socialhost:/:session_prefix::im_prefix:_/widget/render/community?usegapi\u003d1"
                    },
                    "sharetoclassroom": {
                        "url": "https://www.gstatic.com/classroom/sharewidget/widget_stable.html?usegapi\u003d1"
                    },
                    "ytshare": {
                        "params": {
                            "url": ""
                        },
                        "url": ":socialhost:/:session_prefix:_/widget/render/ytshare?usegapi\u003d1"
                    },
                    "plus": {
                        "url": ":socialhost:/:session_prefix:_/widget/render/badge?usegapi\u003d1"
                    },
                    "family_creation": {
                        "params": {
                            "url": ""
                        },
                        "url": "https://families.google.com/webcreation?usegapi\u003d1\u0026usegapi\u003d1"
                    },
                    "commentcount": {
                        "url": ":socialhost:/:session_prefix:_/widget/render/commentcount?usegapi\u003d1"
                    },
                    "configurator": {
                        "url": ":socialhost:/:session_prefix:_/plusbuttonconfigurator?usegapi\u003d1"
                    },
                    "zoomableimage": {
                        "url": "https://ssl.gstatic.com/microscope/embed/"
                    },
                    "appfinder": {
                        "url": "https://gsuite.google.com/:session_prefix:marketplace/appfinder?usegapi\u003d1"
                    },
                    "savetowallet": {
                        "url": "https://pay.google.com/gp/v/widget/save"
                    },
                    "person": {
                        "url": ":socialhost:/:session_prefix:_/widget/render/person?usegapi\u003d1"
                    },
                    "savetodrive": {
                        "url": "https://drive.google.com/savetodrivebutton?usegapi\u003d1",
                        "methods": ["save"]
                    },
                    "page": {
                        "url": ":socialhost:/:session_prefix:_/widget/render/page?usegapi\u003d1"
                    },
                    "card": {
                        "url": ":socialhost:/:session_prefix:_/hovercard/card"
                    }
                }
            },
            "h": "m;/_/scs/apps-static/_/js/k\u003doz.gapi.en.yyhByYeMTAc.O/am\u003dwQc/d\u003d1/ct\u003dzgms/rs\u003dAGLTcCN9qAMm_5_ztFCxaPySR5cb8QjKkw/m\u003d__features__",
            "u": "https://apis.google.com/js/api.js",
            "hee": true,
            "fp": "50939a1cffc34695362a4bdc910d88f4e983656d",
            "dpo": false
        },
        "fp": "50939a1cffc34695362a4bdc910d88f4e983656d",
        "annotation": ["interactivepost", "recobar", "signin2", "autocomplete", "profile"],
        "bimodal": ["signin", "share"]
    }
});

function authenticate() {
  return gapi.auth2.getAuthInstance()
      .signIn({scope: "https://www.googleapis.com/auth/youtube.readonly"})
      .then(function() { console.log("Sign-in successful"); },
            function(err) { console.error("Error signing in", err); });
}

function loadClient() {
  gapi.client.setApiKey("AIzaSyCoTpMozat1rLnBqHPzd2GN4e5NE3al5w8");
  return gapi.client.load("https://www.googleapis.com/discovery/v1/apis/youtube/v3/rest")
      .then(function() { console.log("GAPI client loaded for API"); },
            function(err) { console.error("Error loading GAPI client for API", err); });
}

// Make sure the client is loaded and sign-in is complete before calling this method.
function execute(videoId) {
  return gapi.client.youtube.videos.list({
    "part": [
      "liveStreamingDetails",
      "contentDetails"
    ],
    "id": [
      videoId
    ]
  })
      .then(function(response) {
              // Handle results here (response.result has the parsed body).
              const schedStartTime = response.result.items[0].liveStreamingDetails.scheduledStartTime;
              const schedEndTime = response.result.items[0].liveStreamingDetails.scheduledEndTime;
              const duration = response.result.items[0].contentDetails.duration;
              storeLiveStreamInfo(schedStartTime, schedEndTime, duration)
            },
            function(err) { console.error("Execute error", err); });
}

gapi.load("client:auth2", function() {
  gapi.auth2.init({client_id: "583465356044-j1fls4tnrtpmf24ojrkjmqm4ldvckn4p.apps.googleusercontent.com"});
});

// Sets up the navbar for any page.
function navBarSetup() {
  fetch('/sign-in').then(response => response.json()).then(info => {
    if(info.status) {
      document.getElementById('navbar-dropdown').classList.remove('d-none');
      document.getElementById('sign-in-button').classList.add('d-none');
      document.getElementById('sign-out-link').href = info.url;
      getProfilePicture();
    }
  });
}

// Gets the profile picture for the navbar
function getProfilePicture() {
  fetch('/user').then(response => response.json()).then(userInfo => {
    document.getElementById('profile-pic-nav').src = '/blob?blob-key=' +  userInfo.profilePicKey;
  });
}

function addParagraph(content) {
  return "<p>" + content + "</p>";
}

function shareViaGmail() {
  let msgbody = "Yum!";
  let url = 'https://mail.google.com/mail/?view=cm&fs=1&tf=1&to=&su=Check+out+this+recipe!&body='+msgbody+'&ui=2&tf=1&pli=1';
  window.open(url, 'sharer', 'toolbar=0,status=0,width=648,height=395');
}
