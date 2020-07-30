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
    const linkEl = document.getElementById('sign-in-button-big');
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
  const editableEl = document.getElementById('editable-user-info');
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
  // If there is no associated user (i.e. anon commenter), the userProfile is not hyperlinked,
  // because it does not exist.
  let userProfile = "Anonymous";
  if (comment.username !== "Anonymous" || comment.location !== "Unknown") {
    // Username hyperlinks to the profile of the user who left the comment.
    userProfile = hyperlinkText(comment.username, "/profile-page.html?key=" + comment.userKeyString);
  }
  var userInfoDisplayed = userProfile + " • " + comment.location + " • " + comment.MMDDYYYY;
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
  // rowVars used to dynamically name divs of class row.
  var rowVars = {};
  let liveStreamCount = 0;
  let rowCount = 0;
  fetch('/display-livestreams').then(response => response.json()).then((liveStreams) => {
    const liveStreamGrid = document.getElementById('live-stream-grid');
    liveStreams.forEach((liveStream) => {
      // Every three live streams, create a new row.
      if (liveStreamCount % 3 == 0) {
        rowCount++;
        rowVars['liveStreamRow' + rowCount] = document.createElement('div');
        rowVars['liveStreamRow' + rowCount].className = "row";
      }
      rowVars['liveStreamRow' + rowCount].appendChild(createLiveStreamElement(liveStream));
      liveStreamGrid.appendChild(rowVars['liveStreamRow' + rowCount]);
      liveStreamCount++;
    })
  });
}

/** Creates an element that represents a live stream. */
function createLiveStreamElement(liveStream) {
  const liveStreamItem = document.createElement('div');
  liveStreamItem.className = 'col feed-img-container';
  liveStreamItem.innerHTML += "<img src=" + "https://tinyurl.com/y8eph3n6" + ">";

  const overlay = document.createElement('div');
  overlay.className = "overlay";

  const unorderedList = document.createElement('ul');
  unorderedList.className = "list-unstyled";

  const listElement = document.createElement('li');
  listElement.className = "list-space";
  listElement.innerText= liveStream.liveStreamKey;
  
  unorderedList.appendChild(listElement);
  overlay.appendChild(unorderedList);
  liveStreamItem.appendChild(overlay);
  return liveStreamItem;
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

/** When used for livestream creation, authenticates the user and then
    hides the auth button and shows the livestream creation form. */
function authenticate() {
  return gapi.auth2.getAuthInstance()
      .signIn({scope: "https://www.googleapis.com/auth/youtube.readonly"})
      .then(function() { console.log("Sign-in successful"); },
            function(err) { console.error("Error signing in", err); })
      .then(unhideLivestreamCreation()).then(hideAuthButton());
}

function unhideLivestreamCreation() {
  var livestreamCreation = document.getElementById("livestream-creation");
  livestreamCreation.style.display = "block";
}

function hideAuthButton() {
  var authButton = document.getElementById("auth-button");
  authButton.style.display = "none";
}

function loadClient() {
  fetch('assets/files/api_key.json').then(response => {
    return response.json();
  }).then(data => {
  gapi.client.setApiKey(data.API_KEY);
  return gapi.client.load("https://www.googleapis.com/discovery/v1/apis/youtube/v3/rest")
      .then(function() { console.log("GAPI client loaded for API"); },
            function(err) { console.error("Error loading GAPI client for API", err); });
  }).catch(error => {
    console.log(error);
  });
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
              try {
              const schedStartTime = response.result.items[0].liveStreamingDetails.scheduledStartTime;
              const schedEndTime = response.result.items[0].liveStreamingDetails.scheduledEndTime;
              const duration = response.result.items[0].contentDetails.duration;
              storeLiveStreamInfo(schedStartTime, schedEndTime, duration)
              document.getElementById("invalid-link-err").innerHTML = "";
              document.getElementById("published-msg").innerHTML = "<p>Live stream successfully registered! Please check the 'You' tab under live streams to see if it has been published.*</p>";
              document.getElementById("published-msg").innerHTML += "<p>*If the live stream start or end time is found to be invalid, it will not be published, and will not appear in the 'You' tab or elsewhere on Shef.</p>"
              } catch (err) {
                document.getElementById("invalid-link-err").innerHTML = "<p>Please enter a link to a valid livestream event.</p>";
                document.getElementById("published-msg").innerHTML = "";
              }
            },
            function(err) { console.error("Execute error", err); });
}

gapi.load("client:auth2", function() {
  fetch('assets/files/api_key.json').then(response => {
    return response.json();
  }).then(data => {
  gapi.auth2.init({client_id: data.CLIENT_ID});
  }).catch(error => {
    console.log(error);
  });
});

/** Calendar */

// Array of API discovery doc URLs for APIs used by the quickstart
var DISCOVERY_DOCS = ["https://www.googleapis.com/discovery/v1/apis/calendar/v3/rest"];

// Authorization scopes required by the API; multiple scopes can be
// included, separated by spaces.
var SCOPES = "https://www.googleapis.com/auth/calendar";

/**
  *  On load, called to load the auth2 library and API client library.
  */
function handleClientLoad() {
  gapi.load('client:auth2', initClient());
}

/**
  * Initializes the API client library and sets up sign-in state
  * listeners.
  */
function initClient() {
  fetch('assets/files/api_key.json').then(response => {
    return response.json();
  }).then(data => {
    gapi.client.init({
      apiKey: data.API_KEY,
      clientId: data.CLIENT_ID,
      discoveryDocs: DISCOVERY_DOCS,
      scope: SCOPES
    })
  }).catch(error => {
    console.log(error);
  });
}

/**
  *  Sign in the user upon button click.
  */
function handleAuthClick(event) {
  var authorizeButton = document.getElementById('authorize_button');
  var signoutButton = document.getElementById('signout_button');
  var eventButton = document.getElementById('event_button');
  gapi.auth2.getAuthInstance().signIn();
  authorizeButton.style.display = 'none';
  eventButton.style.display = 'block';
  signoutButton.style.display = 'block';
}

/**
  *  Sign out the user upon button click.
  */
function handleSignoutClick(event) {
  var authorizeButton = document.getElementById('authorize_button');
  var signoutButton = document.getElementById('signout_button');
  var eventButton = document.getElementById('event_button');
  gapi.auth2.getAuthInstance().signOut();
  authorizeButton.style.display = 'block';
  eventButton.style.display = 'none';
  signoutButton.style.display = 'none';
}

/**
  * Append a pre element to the body containing the given message
  * as its text node. Used to display the results of the API call.
  *
  * @param {string} message Text to be placed in pre element.
  */
function appendPre(message) {
  var pre = document.getElementById('content');
  var textContent = document.createTextNode(message + '\n');
  pre.appendChild(textContent);
}

function addEvent(startTime, endTime){
  var event = {
    'summary': 'Google I/O 2015',
    'location': '800 Howard St., San Francisco, CA 94103',
    'description': 'A chance to hear more about Google\'s developer products.',
    'start': {
      'dateTime': startTime,
      'timeZone': 'America/Los_Angeles'
    },
    'end': {
      'dateTime': endTime,
      'timeZone': 'America/Los_Angeles'
    },
    'recurrence': [
      'RRULE:FREQ=DAILY;COUNT=2'
    ],
    'reminders': {
      'useDefault': false,
      'overrides': [
        {'method': 'email', 'minutes': 24 * 60},
        {'method': 'popup', 'minutes': 10}
      ]
    }
  };

  var request = gapi.client.calendar.events.insert({
    'calendarId': 'primary',
    'resource': event
  });

  request.execute(function(event) {
    appendPre('Event created: ' + event.htmlLink);
  });
}

function hyperlinkText(text, link) {
  return "<a href=" + link + ">" + text + "</a>";
}

/** Fetches the recipes associated with the user from the server and adds them
    as options to the DOM. */
function loadUserRecipes() {
  fetch('/fetch-user-recipes').then(response => response.json()).then((recipes) => {
    const recipeList = document.getElementById('recipe-selection');
    recipeCount = 0;
    recipes.forEach((recipe) => {
      var recipeOption = document.createElement('option');
      if (recipeCount === 0) {
        recipeOption.selected = "selected";
        recipeCount++;
      }
      recipeOption.innerHTML += recipe;
      recipeList.appendChild(recipeOption);
    })
  });
}

/** Fetches recipes from the server and adds them to the DOM. */
function loadRecipes() {
  // rowVars used to dynamically name divs of class row, for up to 3 recipes.
  var rowVars = {};
  let recipeCount = 0;
  let rowCount = 0;
  fetch('/display-recipes').then(response => response.json()).then((recipes) => {
    const recipeGrid = document.getElementById('recipe-grid');
    recipes.forEach((recipe) => {
      // Every three live streams, create a new row.
      if (recipeCount % 3 == 0) {
        rowCount++;
        rowVars['recipeRow' + rowCount] = document.createElement('div');
        rowVars['recipeRow' + rowCount].className = "row";
      }
      rowVars['recipeRow' + rowCount].appendChild(createFeedElement(recipe));
      recipeGrid.appendChild(rowVars['recipeRow' + rowCount]);
      recipeCount++;
    })
  });
}
 
/** Creates an element that represents a feed item,
    for example a Recipe or Live Stream. */
function createFeedElement(item) {
  const feedItem = document.createElement('div');
  feedItem.className = 'col feed-img-container';
  // Using a constant image because, as is, recipes doesn't support photos.
  feedItem.innerHTML += "<img src=" + "https://tinyurl.com/y8eph3n6" + ">";
  // On click, redirect to corresponding recipe.
  feedItem.onclick = function() {
    window.location="/recipe.html?key=" + item.key;
  }
 
  const overlay = document.createElement('div');
  overlay.className = "overlay";
 
  const unorderedList = document.createElement('ul');
  unorderedList.className = "list-unstyled";
 
  const listElement = document.createElement('li');
  listElement.className = "list-space";
  listElement.innerText= item.name;
  
  unorderedList.appendChild(listElement);
  overlay.appendChild(unorderedList);
  feedItem.appendChild(overlay);
  return feedItem;
}

function recipePageInit() {
  getRecipeInfo();
  loadComments();
}

function getRecipeInfo() {
  var url = window.location.href;
  var key = url.split('?')[1];

  fetch('/new-recipe?' + key).then(response => response.json()).then(recipe => {
    document.getElementById('recipe-title').innerHTML = recipe.name;
    document.getElementById('recipe-author').innerHTML = recipe.user;
    document.getElementById('recipe-description').innerHTML = recipe.description;
    document.getElementById('recipe-video').innerHTML = getAssociatedLiveStreamLink(key);
    displayTags(recipe.tags);
    displayIngredients(recipe.ingredients);
    displaySteps(recipe.steps);
  });
}

/** Get the link to the live stream associated with the given recipe key. */
function getAssociatedLiveStreamLink(recipeKey) {
  fetch('/fetch-associated-live-stream?recipe-key=' + recipeKey).then(response => response.json()).then((livestreams) => {
    console.log("Fetching live stream link... " + livestreams.length);
    // There should only be one live stream.
    livestreams.forEach((livestream) => {
      return result.link;
    })
  });
}

function addLiveStreamToCalendar() {
  var key = url.split('?')[1];
  fetch('/fetch-associated-live-stream?recipe-key=' + key).then(response => response.json()).then((livestreams => {
    console.log("Adding live stream to calendar... " + livestreams.length);
    // There should only be one live stream.
    livestreams.forEach((livestream) => {
      addEvent(livestream.startTime, livestream.endTime)
    })
  }));
}

/** Formats and displays tags on the page. */
function displayTags(tagsList) {
  var tagSection = document.getElementById('recipe-tags');
  let tagCount = 0;
  tagsList.forEach((tag) => {
    tagCount++;
    tagSection.innerHTML += "#" + tag
    if (tagCount < tagsList.length) {
      tagSection.innerHTML += ", ";
    }
  });
}

/** Formats and displays ingredients, with corresponding checkboxes, on the page. */
function displayIngredients(ingredList) {
  var ingredElements = {};  // ingredElements used to dynamically name divs of class form-check small-sep, for a single ingredient.
  let ingredCount = 0;
  var ingredSection = document.getElementById('recipe-ingredients');
  ingredList.forEach((ingredient) => {
    ingredElements['ingredElement' + ingredCount] = document.createElement('div');
    ingredElements['ingredElement' + ingredCount].className = "form-check small-sep";

    // Create a checkbox.
    var input = document.createElement("input");
    input.type = "checkbox";
    input.class = "form-check-input";
    // Label the checkbox with the individual ingredient.
    var label = document.createElement("label");
    label.label = "form-check-label";
    label.innerHTML = "<p>" + ingredient + "</p>";

    ingredElements['ingredElement' + ingredCount].appendChild(input);
    ingredElements['ingredElement' + ingredCount].appendChild(label);
    ingredSection.appendChild(ingredElements['ingredElement' + ingredCount]);
    ingredCount++;
  });
}

/** Formats and displays steps on the page. */
function displaySteps(stepList) {
  var rowVars = {};  // rowVars used to dynamically name divs of class row, for a single step.
  let stepCount = 1;
  var stepSection = document.getElementById('recipe-steps');
  stepList.forEach((step) => {
    rowVars['stepElement' + stepCount] = document.createElement('div');
    rowVars['stepElement' + stepCount].className = "row";

    // Create and format the step number.
    var stepNumElement = document.createElement("div");
    stepNumElement.class = "col-sm-2 col-md-2 col-lg-2";
    var stepNum = document.createElement("h3");
    stepNum.innerText += stepCount + ". ";
    stepNumElement.appendChild(stepNum);
    // Create and format the step text.
    var stepTextElement = document.createElement("p");
    stepTextElement.class = "col-sm-10 col-md-10 col-lg-10";
    stepTextElement.innerHTML = step;

    rowVars['stepElement' + stepCount].appendChild(stepNumElement);
    rowVars['stepElement' + stepCount].appendChild(stepTextElement);
    stepSection.appendChild(rowVars['stepElement' + stepCount]);
    stepCount++;
  });
}

function addParagraph(content) {
  return "<p>" + content + "</p>";
}

/** Redirects the user to the result page, with the given parameter for user-query. */
function redirectToResults(userQuery) {
  document.location.href = "search-results-test.html?user-query=" + userQuery;
  return false;
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

/** Gets the value of the given parameter from the current URL string. */
function getURLParamVal(param) {
  let searchParams = (new URL(document.location)).searchParams;
  return searchParams.get(param);
}

/** Creates an element to display a result. */
function createResultElement(result) {
  const resultElement = document.createElement('li');
  resultElement.className = 'result';

  const resultId = document.createElement('span');
  resultId.innerText = result.id;

  resultElement.appendChild(resultId);
  return resultElement;
}

/** Loads options from Datastore and displays them using Datalist
    as autofill suggestions to the user. HTML5 Datalist itself does not
    currently support multiple select so this is a workaround.
    loadAutofill is chained here because it must happen AFTER the
    options are loaded in from Datastore; this way, the user continues
    to get autofill suggestions past the first keyword entered. */
function loadOptions() {
  var optionList = document.getElementById('allOptions');
  fetch('/fetch-options').then(response => response.json()).then((options) => {
    options.forEach(function(option) {
      var singleOption = document.createElement('option');
      singleOption.value = option;
      optionList.appendChild(singleOption);
    })
  })
  .then(() => {
    loadAutofill();
  });
}

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

// Gets the profile picture for the navbar.
function getProfilePicture() {
  fetch('/user').then(response => response.json()).then(userInfo => {
      document.getElementById('profile-pic-nav').src = '/blob?blob-key=' +  userInfo.profilePicKey;
  });
}

const searchSep = ','; // Character used to separate keywords.
function loadAutofill() {
  for (const input of document.getElementsByTagName("input")) {
    if (input.list instanceof HTMLDataListElement) {
      const options = Array.from(input.list.options).map(opt => opt.value);
      let keywordCount = input.value.split(searchSep).length;
      input.addEventListener("input", () => {
      const currkeywordCount = input.value.split(searchSep).length;
      if (keywordCount !== currkeywordCount) {  // User added a separator.
        const lastSepIdx = input.value.lastIndexOf(searchSep);
        let existingInput = "";
        if (lastSepIdx !== -1) {
          existingInput = input.value.substr(0, lastSepIdx) + searchSep;
        }
        refillDatalist(input, options, existingInput);
        keywordCount = currkeywordCount;
        }
      });
    }
  }
}

/** Reloads Datalist options after user enters a new keyword,
    such that existing user input is not shown in the Datalist. */
function refillDatalist(input, options, existingInput) {
  const list = input.list;
  if (list && options.length > 0) {
    list.innerHTML = "";
    const usedOptions = existingInput.split(searchSep).map(value => value.trim());
    for (const option of options) {
      if (usedOptions.indexOf(option) < 0) {
        const optionElement = document.createElement("option");
        optionElement.value = existingInput + " " + option;
        list.append(optionElement);
      }
    }
  }
}

function shareViaGmail() {
  let msgbody = "Yum!";
  let url = 'https://mail.google.com/mail/?view=cm&fs=1&tf=1&to=&su=Check+out+this+recipe!&body='+msgbody+'&ui=2&tf=1&pli=1';
  window.open(url, 'sharer', 'toolbar=0,status=0,width=648,height=395');
}

/** @class A custom element that represents a parameter input. */
class ParameterInput extends HTMLElement {
  constructor() {
    super();
    this.label = document.createElement('label');
    this.textArea = document.createElement('textarea');
    this.addButton = document.createElement('button');
    this.deleteButton = document.createElement('button');
    this.container = document.createElement('div');

    this.container.appendChild(this.label);
    this.container.appendChild(this.textArea);
    this.container.appendChild(this.addButton);
    this.container.appendChild(this.deleteButton);
  }

  /** Once the attributes for the ParameterInput exist, set its values accordingly. */
  connectedCallback() {
    this.name = this.getAttribute('name');
    this.index = parseInt(this.getAttribute('index'));
    this.parent = this.name + 's';

    this.textArea.rows = '1';
    this.addButton.type = 'button';
    this.addButton.innerText = 'Add ' + this.name;
    this.deleteButton.type = 'button';
    this.deleteButton.innerText = 'Delete ' + this.name;
    this.setIndexAttributes();

    this.appendChild(this.container);
  }

  /**
   * Sets all attributes that depend on the ParameterInput's index.
   * These attributes are separated into their own method so that they can be updated
   * when ParameterInputs change position.
   */
  setIndexAttributes() {
    var paramName = this.name.toLowerCase() + this.index;
    this.id = this.name + this.index;
    this.label.innerText = this.name + ' ' + (this.index + 1);
    this.label.for = paramName;
    this.textArea.name = paramName;

    // Inserts a new ParameterInput below the one clicked.
    this.addButton.onclick = event => {
      var newParameter = createParameterInput(this.name, this.index + 1);
      insertParameterInput(this, newParameter);
    }

    // Deletes the ParameterInput clicked.
    this.deleteButton.onclick = event => {
      const fieldName = this.field;
      const startIndex = this.index;
      this.remove();
      updateIndices(fieldName, startIndex);
    }
  }

  /** Gets the text in a ParameterInput's text area. */
  get text() {
    return this.textArea.value;
  }

  /** Gets the ParameterInput's index. */
  get position() {
    return this.index;
  }

  /** Gets the parent field of the ParamterInput (Tags, Ingredients, or Steps). */
  get field() {
    return this.parent;
  }

  /** Sets the text in a ParameterInput's text area. */
  set text(value) {
    this.textArea.value = value;
  }

  /** Sets the ParameterInput's index. */
  set position(value)  {
    this.index = parseInt(value);
  }

  /** Sets the parent field of the ParamterInput (Tags, Ingredients, or Steps). */
  set field(value) {
    this.parent = value;
  }
}
customElements.define('parameter-input', ParameterInput);

/** 
 * Creates a ParameterInput.
 * @param {string} name Used for displaying and determining the parent field.
 * @param {string} index The position of the ParameterInput.
 */
function createParameterInput(name, index) {
  var newParameter = document.createElement('parameter-input');
  newParameter.setAttribute('name', name);
  newParameter.setAttribute('index', index);
  newParameter.setAttribute('id', name + index);
  return newParameter;
}

/** 
 * Inserts a ParameterInput.
 * @param {ParameterInput} previous The ParameterInput to insert after.
 * @param {ParameterInput} parameterInput The ParameterInput to insert.
 */
function insertParameterInput(previous, parameterInput) {
  previous.insertAdjacentElement('afterend', parameterInput);
  updateIndices(parameterInput.field, parameterInput.position + 1);
}

/** 
 * Appends an existing ParameterInput.
 * @param {string} fieldName The field to append to.
 * @param {ParameterInput} parameterInput The ParameterInput to append.
 */
function appendParameterInput(fieldName, parameterInput) {
  const field = document.getElementById(fieldName);
  field.appendChild(parameterInput);
}

/** 
 * Appends a new ParameterInput.
 * @param {string} fieldName The field to append to.
 */
function appendNewParameterInput(fieldName) {
  const field = document.getElementById(fieldName);
  field.appendChild(createParameterInput(fieldName.slice(0, -1), field.children.length));
}

/**
 * Updates the indices of ParameterInputs after an insertion or deletion.
 * @param {string} fieldName The field with the ParameterInputs to update.
 * @param {number} startIndex The index of the first ParameterInput to update.
 */
function updateIndices(fieldName, startIndex) {
  var parameters = document.getElementById(fieldName).children;
  for (var i = startIndex; i < parameters.length; i++) {
    parameters[i].position = i;
    parameters[i].setIndexAttributes();
  }
}

/** Gets a parent recipe's data from Datastore. */
function getOriginalRecipe() {
  const key = document.getElementById('key').value;
  if (key) {
    fetch('/new-recipe?key=' + key).then(response => response.json()).then((recipe) => {
      populateRecipeCreationForm(recipe);
    });
  }
}

/** Populates the fields of the recipe editor with a parent recipe's data. */
function populateRecipeCreationForm(recipe) {
  document.getElementById('name').value = recipe.name;
  document.getElementById('description').value = recipe.description;
  populateFormField('Tag', recipe.tags);
  populateFormField('Ingredient', recipe.ingredients);
  populateFormField('Step', recipe.steps);
}

function populateFormField(fieldName, data) {
  for (var i = 0; i < data.length; i++) {
    var parameter = document.getElementById(fieldName + i);
    if (parameter !== null) {
      parameter.text = data[i];
    } else {
      var newParameter = createParameterInput(fieldName, i);
      newParameter.text = data[i];
      appendParameterInput(fieldName + 's', newParameter);
    }
  }
}

/** Called by every page that requires the user to be logged in order to access. */
function protectPage() {
  fetch('/sign-in-status').then(response => response.text()).then((signedIn) => {
    if(signedIn === "false") {
      window.location.replace("/index.html");
    }
  });
}
