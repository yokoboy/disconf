var env_app; //
var mainTpl; // 表格模版
var api = {
    batch_download: "/api/web/config/downloadfilebatch?env_app="
};

(function ($) {

    getSession();
    cleanPage();

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

    //
    // 渲染主列表
    //
    function main() {
        cleanPage();

        $("#zk_deploy").show().children().show();

        $("#batch_download").attr('href', "/api/web/config/downloadfilebatch?appId=" + appId + "&envId=" + envId + "&version=" + version);

        $("#mainlist_error").hide();
        var parameter = ""

        url = "/api/web/config/list";
        if (appId == null && envId == null && version == null) {

        } else {
            url += "?";
            if (appId != -1) {
                url += "appId=" + appId + "&";
            }
            if (envId != -1) {
                url += "envId=" + envId + "&";
            }
            if (version != "#") {
                url += "version=" + version + "&";
            }
        }

        $.ajax({type: "GET", url: url}).done(function (data) {
            if (data.success === "true") {
                var html = "";
                var result = data.page.result;
                $.each(result, function (index, item) {
                    html += renderItem(item, index);
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
        });
        // 渲染主列表
        function renderItem(item, i) {

            var link = "";
            del_link = '<a id="itemDel' + item.configId + '" style="cursor: pointer; cursor: hand; " ><i title="删除" class="icon-remove"></i></a>';
            if (item.type == "配置文件") {
                link = '<a target="_blank" href="modifyFile.html?configId=' + item.configId + '"><i title="修改" class="icon-edit"></i></a>';
            } else {
                link = '<a target="_blank" href="modifyItem.html?configId=' + item.configId + '"><i title="修改" class="icon-edit"></i></a>';
            }
            var downloadlink = '<a href="/api/web/config/download/' + +item.configId + '"><i title="下载" class="icon-download-alt"></i></a>';

            var type = '<i title="配置项" class="icon-leaf"></i>';
            if (item.type == "配置文件") {
                type = '<i title="配置文件" class="icon-file"></i>';
            }

            var data_fetch_url = '<a href="javascript:void(0);" class="valuefetch' + item.configId + '" data-placement="left">点击获取</a>'

            var isRight = "OK";
            var style = "";
            if (item.errorNum > 0) {
                isRight = "; 其中" + item.errorNum + "台出现错误";
                style = "text-error";
            }
            var machine_url = '<a href="javascript:void(0);" class="' + style + ' machineinfo' + item.configId + '" data-placement="left">' + item.machineSize + '台 ' + isRight + '</a>'

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
            url: "/api/web/config/" + configId
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
            url: "/api/web/config/zk/" + configId
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
            url: "/api/web/config/" + id
        }).done(function (data) {
            if (data.success === "true") {
                main();
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

        var base_url = "/api/zoo/zkdeploy?appId=" + appId + "&envId=" + envId
            + "&version=" + version

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
