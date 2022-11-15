// ==UserScript==
// @name         ServiceNow Helper
// @namespace    Q
// @version      0.1
// @description  ServiceNow secondary copy button
// @author       You
// @match      https://digitalservices.mercedes-benz.com/*
// @icon         https://www.google.com/s2/favicons?sz=64&domain=mercedes-benz.com
// @grant        GM_xmlhttpRequest
// ==/UserScript==

(function (window) {
    const localStorage = window.localStorage;
    const responseContents = [];
    const buttonId = 'copy_button_identifier';
    const buttonStyles = {
        position: 'fixed',
        'z-index': 9999,
        top: '220px',
        right: '24px',
        width: '110px',
        color: '#fff',
        'font-size': '14px',
        'font-weight': '500',
        'background-color': '#409eff',
        'border-color': 'transparent',
        'outline': 0,
        'border-radius': '9px',
    };
    const squadSelectId = 'squad_select';
    const squadSelectStyles = {
        position: 'fixed',
        top: '220px',
        right: '154px',
        width: '130px',
        outline: 0,
        background: '#409eff',
        color: '#fff',
        padding: '4px',
        'border-radius': '9px',
    };
    const squadEnum = Object.freeze({
        PARTS: "PARTS",
        RWO: "RWO",
        SALES: "SALES",
        ACCOUNTING: "ACCOUNTING",
        WARRANTY: "WARRANTY",
        WORKSHOP: "WORKSHOP",
        OPERATION: "OPERATION",
        ACCIDENT: "ACCIDENT"
    });

    const syncTrelloUrl = "http://10.205.129.7:8080/api/sn-connector/trello/cards";
    let contactUserD8Account = null;

    function changeButton(button, buttonText, styleCursor, color) {
        button.innerHTML = buttonText;
        button.style.cursor = styleCursor;
        button.style['background-color'] = color;
    }

    function syncDataToTrello(data) {
        const button = document.getElementById(buttonId);
        changeButton(button, '正在同步中', 'not-allowed', '#ccc');
        const ticketFullDescription = getTicketDescription();
        const currentSelectSquad = getSquadSelectValue();
        localStorage.setItem('previousSquadSelect', currentSelectSquad);
        const syncDataToTrelloUrl = `${syncTrelloUrl}?squad=${currentSelectSquad}`;
        const request_body = JSON.parse(data);
        request_body['ticketFullDescription'] = ticketFullDescription;
        setTimeout(() => {
            request_body['contactUserD8Account'] = contactUserD8Account;
            console.log(request_body);
            GM_xmlhttpRequest({
                method: "post",
                url: syncDataToTrelloUrl,
                data: JSON.stringify(request_body),
                headers:  {
                    "Content-Type": "application/json"
                },
                onload: function(res) {
                    if(res.status === 200){
                        console.log("同步成功");
                        console.log(res)
                        changeButton(button, '已同步', 'not-allowed', '#ccc');
                    } else {
                        console.log('同步失败')
                        console.log(res)
                        changeButton(button, '同步失败', 'not-allowed', '#ccc');
                        setTimeout(() => {
                            changeButton(button, '同步至Trello', 'default', '#409eff');
                        }, 2000);
                    }
                },
                onerror : function(err){
                    console.log('error')
                    console.log(err)
                    changeButton(button, '同步失败', 'not-allowed', '#ccc');
                    setTimeout(() => {
                            hangeButton(button, '同步至Trello', 'default', '#409eff');
                        }, 2000);
                }
            });
        }, 500);
    }

    function getTicketDescription() {
        return document.querySelector("#sys_original\\.sn_customerservice_case\\.description").value;
    }

    function getContactUserD8Account() {
        const contact = document.querySelector("#viewr\\.sn_customerservice_case\\.contact");
        contact.click();
        setTimeout(() => {
            contact.click();
        }, 200)
        setTimeout(() => {
            const userD8Account = document.querySelector("#customer_contact\\.user_name");
            contactUserD8Account = userD8Account.value;
            contact.click();
        }, 500);
        
    }

    const getSquadSelectValue = () => {
        return document.querySelector(".squad_select_class").value;
    }


    function createButton(id, styles) {
        const button = document.createElement('button');
        button.setAttribute('type', 'button');
        button.setAttribute('id', buttonId);
        button.innerHTML = '同步至Trello';
        button.onclick = function() {
            const { length } = responseContents;
            if (length) {
                getContactUserD8Account();
                syncDataToTrello(responseContents[length - 1].content);

            }
        }
        Object.keys(buttonStyles).forEach(key => {
            button.style[key] = buttonStyles[key];
        });
        document.body.appendChild(button);
    }

    const createSquadSelect = () => {
        const squadSelect = document.createElement('div');
        const previousSquadSelect = localStorage.getItem('previousSquadSelect');
        const options = Object.keys(squadEnum).map(squad => {
            if (squad === previousSquadSelect) {
                return `<option value =\"${squadEnum[squad]}\" selected>${squad}</option>`
            }
            return `<option value =\"${squadEnum[squad]}\">${squad}</option>`
        }).join('\n');
        squadSelect.innerHTML = `<select class='squad_select_class' style='width: 120px; height: 26px'>${options}</select>`;
        squadSelect.setAttribute('id', squadSelectId);
        Object.keys(squadSelectStyles).forEach(key => {
            squadSelect.style[key] = squadSelectStyles[key];
        });
        document.body.appendChild(squadSelect);
    }

    if (window.location.pathname.endsWith('sn_customerservice_case.do')) {
        createButton(buttonId, buttonStyles);
        createSquadSelect();
    }

    (function (open) {
        XMLHttpRequest.prototype.open = function (...args) {
            if (args[1].endsWith('angular.do')) {
                // console.log('args', args)
                this.addEventListener("readystatechange", function (event) {
                    responseContents.push({ readyState: this.readyState, content: event.currentTarget.responseText });
                    // console.log('ServiceNow readystatechange', this.readyState, event.currentTarget.responseText);
                }, false);
            }

            open.apply(this, args);
        };
    })(XMLHttpRequest.prototype.open);
})(window)
