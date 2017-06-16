var mainTpl; // 表格模版
var env_app; // 表格模版
var api = {
    can_opt: "/api/auth_mng/env_app_opt?env_app=",
    // 批量下载
    batch_download: "/api/web/config/downloadfilebatch?env_app=",
    config_list: "/api/web/config/list?env_app="
};
var authInfo;
// zTree 的参数配置，深入使用请参考 API 文档（setting 配置详解）


(function ($) {

    getSession();
    cleanPage();

    var setting = {
        data: {
            simpleData: {
                enable: true
            }
        }, callback: {
            onClick: zTreeOnClick
        }
    };

    function zTreeOnClick(event, treeId, treeNode) {
        if (!treeNode.isParent) {
            env_app = treeNode.id;
            main(treeNode.id);

            removeCantOpt(treeNode.id);
        }
    }

    // 索取所有角色
    $.getJSON("/api/auth_mng/env_app", function (data, status) {
        authInfo = $.fn.zTree.init($("#authInfo"), setting, data.result.envApp);
    });


    mainTpl = $("#tbodyTpl").html();

    function tip(info) {
        $("#mainlist_error").text(info).show();
    }

    function cleanPage() {
        // 提示
        tip("请选择环境和应用");
        // 清空表格内容
        $("#accountBody").html("");
        // 隐藏表格
        $("#mainlist").hide();
        // 隐藏公共按钮
        $("#zk_deploy").hide();
        $("#batch_download").attr("href", "###");
        $("#zk_deploy_button").unbind("click"); //
        $("#zk_deploy_info").hide();
        $("#zk_deploy_info_pre").html("");


    }

    function removeCantOpt(env_app) {
        $.getJSON(api.can_opt + env_app, function (data) {
            var canOpt = data.result.opt;
            var allOpt = ["1", "2", "3", "4", "5", "6"];
            var cantOpt = [];
            for (var i in allOpt) {
                var exist = false;
                for (var j in canOpt) {
                    if (allOpt[i] == canOpt[j]) {
                        exist = true;
                    }
                }
                if (!exist) {
                    cantOpt.push(allOpt[i]);
                }
            }
            console.info(canOpt)
            console.info(cantOpt)

            for (var index in cantOpt) {
                console.info(".opt_" + cantOpt[index])
                $(".opt_" + cantOpt[index]).hide();
            }
        });
    }


    //
    // 渲染主列表
    //
    function main(env_app) {
        cleanPage();
        if (!env_app) {
            return;
        }

        $("#zk_deploy").show().children().show();

        $("#batch_download").attr('href', api.batch_download + env_app);

        $("#mainlist_error").hide();

        $.ajax({type: "GET", url: api.config_list + env_app}).done(function (data) {
            // console.log(data)

            if (data.success === "true") {
                var html = "";
                var result = data.result;
                $.each(result, function (index, item) {
                    html += renderItem(env_app, item, index);
                });
                if (html != "") {
                    $("#mainlist").show();
                    $("#accountBody").html(html);
                } else {
                    $("#accountBody").html("");
                }

            } else {
                $("#accountBody").html("");
                $("#mainlist").hide();
            }

            bindDetailEvent(result);

            // ZK绑定情况
            fetchZkDeploy();

            removeCantOpt(env_app)
        });
        // 渲染主列表
        function renderItem(env_app, item, i) {

            var link = "";
            del_link = '<a id="itemDel' + item.configId + '" class="opt_5" style="cursor: pointer; cursor: hand; " ><i title="删除" class="icon-remove"></i></a>';
            if (item.type == "配置文件") {
                link = '<a target="_blank" class="opt_2" href="modifyFile.html?configId=' + item.configId + '&env_app=' + env_app + '"><i title="修改" class="icon-edit"></i></a>';
            } else {
                link = '<a target="_blank" class="opt_2" href="modifyItem.html?configId=' + item.configId + '&env_app=' + env_app + '"><i title="修改" class="icon-edit"></i></a>';
            }
            var downloadlink = '<a href="/api/web/config/download/' + +item.configId + '?env_app=' + env_app + '" class="opt_3"><i title="下载" class="icon-download-alt"></i></a>';

            var type = '<i title="配置项" class="icon-leaf"></i>';
            if (item.type == "配置文件") {
                type = '<i title="配置文件" class="icon-file"></i>';
            }

            var data_fetch_url = '<a href="javascript:void(0);" class="opt_1 valuefetch' + item.configId + '" data-placement="left">点击获取</a>'

            var isRight = "OK";
            var style = "";
            if (item.errorNum > 0) {
                isRight = "; 其中" + item.errorNum + "台出现错误";
                style = "text-error";
            }
            var machine_url = '<a href="javascript:void(0);" class="' + style + ' machineinfo' + item.configId + '  opt_6" data-placement="left">' + item.machineSize + '台 ' + isRight + '</a>'

            return Util.string.format(mainTpl, '',
                item.appId, // 1
                item.version,// 2
                item.envId,// 3
                item.envName,// 4
                type,// 5
                item.key,// 6
                item.createTime,// 7
                item.modifyTime,// 8
                item.value,// 9
                link,// 10
                del_link,// 11
                i + 1,// 12
                downloadlink,// 13
                data_fetch_url,// 14
                machine_url// 15
            );
        }
    }

    /**
     * @param result
     * @returns {String}
     */
    function getMachineList(machinelist) {

        var tip;
        if (machinelist.length == 0) {
            tip = "";
        } else {
            tip = '<div style="overflow-y:scroll;max-height:400px;"><table class="table-bordered"><tr><th>机器</th><th>值</th><th>状态</th></tr>';
            for (var i = 0; i < machinelist.length; i++) {
                var item = machinelist[i];

                var flag = "正常";
                var style = "";
                if (item.errorList.length != 0) {
                    flag = "错误";
                    style = "text-error";
                }

                tip += '<tr><td><pre>' + item.machine + " </pre></td><td><pre>"
                    + item.value + '</pre></td><td><pre class="' + style
                    + '">' + flag + ": " + item.errorList.join(",")
                    + "</pre></td></tr>";
            }
            tip += '</table></div>';
        }
        return tip;
    }

    //
    // 渲染 配置 value
    //
    function fetchConfigValue(configId, object) {
        //
        // 获取APP信息
        //
        $.ajax({
            type: "GET",
            url: "/api/web/config/" + configId + "?env_app=" + env_app
        }).done(
            function (data) {
                if (data.success === "true") {
                    var result = data.result;

                    var e = object;
                    e.popover({
                        content: "<pre>" + Util.input.escapeHtml(result.value) + "</pre>",
                        html: true
                    }).popover('show');
                }
            });
    }

    //
    // 获取 ZK
    //
    function fetchZkInfo(configId, object) {
        //
        // 获取APP信息
        //
        $.ajax({
            type: "GET",
            url: "/api/web/config/zk/" + configId + "?env_app=" + env_app
        }).done(
            function (data) {
                if (data.success === "true") {
                    var result = data.result;

                    var e = object;
                    e.popover({
                        content: getMachineList(result.datalist),
                        html: true
                    }).popover('show');
                }
            });
    }

    // 详细列表绑定事件
    function bindDetailEvent(result) {
        if (result == null) {
            return;
        }
        $.each(result, function (index, item) {
            var id = item.configId;

            // 绑定删除事件
            $("#itemDel" + id).on("click", function (e) {
                deleteDetailTable(id, item.key);
            });

            $(".valuefetch" + id).on('click', function () {
                var e = $(this);
                e.unbind('click');
                fetchConfigValue(id, e);
            });

            $(".machineinfo" + id).on('click', function () {
                var e = $(this);
                e.unbind('click');
                fetchZkInfo(id, e);
            });

        });

    }

    // 删除
    function deleteDetailTable(id, name) {

        var ret = confirm("你确定要删除吗 " + name + "?");
        if (ret == false) {
            return false;
        }

        $.ajax({
            type: "DELETE",
            url: "/api/web/config/" + id + "?env_app=" + env_app
        }).done(function (data) {
            if (data.success === "true") {
                main(env_app);
            }
        });
    }

    //
    function fetchZkDeploy() {
        if ($("#zk_deploy_info").is(':hidden')) {
            var cc = '';
        } else {
            fetchZkDeployInfo();
        }
    }

    //
    // 获取ZK数据信息
    //
    function fetchZkDeployInfo() {

        $("#zk_deploy_info_pre").html("正在获取ZK信息，请稍等......");

        // 参数不正确，清空列表
        if (appId == -1 || envId == -1 || version == "#") {
            $("#zk_deploy_info_pre").html("无ZK信息");
            return;
        }

        var base_url = "/api/zoo/zkdeploy?appId=" + appId + "&envId=" + envId + "&version=" + version + "&env_app=" + env_app

        $.ajax({
            type: "GET",
            url: base_url
        }).done(function (data) {
            if (data.success === "true") {
                var html = data.result.hostInfo;
                if (html == "") {
                    $("#zk_deploy_info_pre").html("无ZK信息");
                } else {
                    $("#zk_deploy_info_pre").html(html);
                }
            }
        });
    }

    $("#zk_deploy_button").on('click', function () {
        $("#zk_deploy_info").toggle();
        fetchZkDeploy();
    });

})(jQuery);
