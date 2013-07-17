Ext.define('Ozone.components.window.MyPageTip', {
    extend: 'Ext.tip.ToolTip',
    alias: 'widget.mypagetip',
    clickedDashboard: null,
    event:null,
    cls: 'ozonequicktip itemTip',
    shadow: false,
    closable:true,
    autoHide:false,
    draggable:false,
    listeners: {
        'close':function(){
            this.destroy();
        }
    },

    dashboardContainer: null,
    appsWindow: null,
    $dashboard: null,
    
    getToolTip: function () {
        var me = this;
        var icn = me.clickedDashboard.iconImageUrl && me.clickedDashboard.iconImageUrl !=' ' ? '<img height=\'64\' width=\'64\' style=\'padding-right:15px;\' src=\''+me.clickedDashboard.iconImageUrl+'\' />':'';
        var str = '<div class=\'dashboard-tooltip-content\'>' + 
                '<h3 class=\'name\'>' + icn + Ext.htmlEncode(Ext.htmlEncode(me.clickedDashboard.name)) + '</h3>';

        me.clickedDashboard.description && (str += '<p class=\'tip-description\'>' + Ext.htmlEncode(Ext.htmlEncode(me.clickedDashboard.description)) +'</p><br>');
        
        // append buttons 
        str += '<ul style=\'padding-top:2%;\'>' +
                    '<li class=\'restoreButton actionButton liPageAdjust\' style=\'border-radius: 0 0 0 10px;\'>'+
                        '<span class=\'restoreImg imgPageAdjust\' ></span>'+
                        '<p class=\'actionText\'>Restore</p>'+
                    '</li>'+
                    '<li class=\'editButton actionButton liPageAdjust\'>'+
                        '<span class=\'editImg imgPageAdjust\'></span>'+
                        '<p class=\'actionText\'>Edit</p>'+
                    '</li>'+
                    '<li class=\'deleteButton actionButton liPageAdjust\'  style=\'border-radius: 0 0 10px; 0\'>'+
                        '<span class=\'deleteImg imgPageAdjust\'></span>'+
                        '<p class=\'actionText\'>Delete</p>'+
                    '</li>'+
               '</ul>' +
              '</div>';
         
        return str;
    },
    
    initComponent: function() {
        var me = this;
        
        me.target = me.event.target.parentElement.id;
        me.html = me.getToolTip();

        me.callParent(arguments);
    },

    setupClickHandlers : function() {
        var me = this,
            $ = jQuery;

        $(me.getEl().dom)
            .on('click', '.editButton', $.proxy(me.editPage, me))
            .on('click', '.deleteButton', $.proxy(me.deletePage, me))
            .on('click', '.restoreButton', $.proxy(me.restorePage, me));
    },

    onRender: function() {
        this.callParent(arguments);
        this.setupClickHandlers();
    },


    editPage: function (evt) {
        evt.stopPropagation();

        var dashboard = this.clickedDashboard;

        var editDashWindow = Ext.widget('createdashboardwindow', {
            itemId: 'editDashWindow',
            title: 'Edit Dashboard',
            height: 250,
            dashboardContainer: this.dashboardContainer,
            ownerCt: this.dashboardContainer,
            hideViewSelectRadio: true,
            existingDashboardRecord: dashboard.model
        }).show();

        this.close();
        this.appsWindow.close();
    },

    deletePage: function (evt) {
        evt.stopPropagation();

        var dashboard = this.clickedDashboard,
            dashboardStore = this.appsWindow.dashboardStore,
            me = this,
            msg;

        function focusEl () {
            evt.currentTarget.focus();
        }

        // Only allow the App owner to delete an App page
        if(dashboard.stack && Ozone.config.user.displayName !== dashboard.stack.owner.username) {
            this.appsWindow.warn('Users cannot remove individual pages from an App. Please contact your administrator.', focusEl);
            return;
        }

        // Only allow deleting a dashboard if its only group is a stack (and we applied the stack membership rule before)
        if(!dashboard.groups || dashboard.groups.length == 0 || (dashboard.groups.length == 1 && dashboard.groups[0].stackDefault)) {
            msg = 'This action will permanently delete <span class="heading-bold">' + Ext.htmlEncode(dashboard.name) + '</span>.';

            this.appsWindow.warn(msg, function () {
                dashboardStore.remove(dashboard.model);
                dashboardStore.save();
                me.appsWindow.notify('Delete Dashboard', '<span class="heading-bold">' + Ext.htmlEncode(dashboard.name) + '</span> deleted!');
                me.appsWindow.reloadDashboards = true;
                var $prev = me.$dashboard.prev();
                me.$dashboard.remove();
                $prev.focus();

            }, focusEl);
        } else {
            this.appsWindow.warn('Users cannot remove dashboards assigned to a group. Please contact your administrator.', focusEl);
        }
        this.close();
    },

    restorePage: function (evt) {
        evt.stopPropagation();
        var me = this,
            $dashboard = this.$dashboard,
            dashboard = this.clickedDashboard,
            dashboardGuid = dashboard.guid;

        this.appsWindow.warn('This action will return the dashboard <span class="heading-bold">' + Ext.htmlEncode(dashboard.name) + '</span> to its current default state. If an administrator changed the dashboard after it was assigned to you, the default state may differ from the one that originally appeared in your Switcher.', function () {
            Ext.Ajax.request({
                url: Ozone.util.contextPath() + '/dashboard/restore',
                params: {
                    guid: dashboardGuid,
                    isdefault: dashboardGuid == me.appsWindow.activeDashboard.guid
                },
                success: function(response, opts) {
                    var json = Ext.decode(response.responseText);
                    if (json != null && json.data != null && json.data.length > 0) {
                        me.appsWindow.notify('Restore Dashboard', '<span class="heading-bold">' + Ext.htmlEncode(dashboard.name) + '</span> is restored successfully to its default state!');

                        var name = json.data[0].name,
                            description = json.data[0].description;

                        dashboard.model.set({
                            'name': name,
                            'description': description
                        });
                        dashboard.name = name;
                        dashboard.description = name;

                        me.appsWindow.updateDashboardEl($dashboard, dashboard);

                        me.appsWindow.reloadDashboards = true;
                    }
                },
                failure: function(response, opts) {
                    Ozone.Msg.alert('Dashboard Manager', "Error restoring dashboard.", function() {
                        Ext.defer(function() {
                            $dashboard[0].focus();
                        }, 200, me);
                    }, me, null, me.dashboardContainer.modalWindowManager);
                    return;
                }
            });
        }, function () {
            evt.currentTarget.focus();
        });
        this.close();
    }
});
