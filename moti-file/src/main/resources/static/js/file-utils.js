/**
 * 文件操作工具类
 * 用于处理文件下载、删除、重命名等操作
 */

// 获取当前用户信息
function getCurrentUser() {
    var currentUser = sessionStorage.getItem('currentUser');
    if (!currentUser) {
        alert('请先登录！');
        window.location.href = '/';
        return null;
    }
    return JSON.parse(currentUser);
}

// 页面状态管理
var pageState = {
    isUploading: false,
    hasUnsavedChanges: false
};

// beforeunload事件处理函数
function beforeUnloadHandler(e) {
    console.log('beforeUnloadHandler triggered, pageState:', pageState);
    e.preventDefault();
    e.returnValue = '';
    return '';
}

// 设置页面状态
function setPageState(key, value) {
    console.log('setPageState:', key, '=', value);
    pageState[key] = value;
    updateBeforeUnload();
}

// 更新beforeunload事件处理
function updateBeforeUnload() {
    if (pageState.isUploading || pageState.hasUnsavedChanges) {
        console.log('Adding beforeunload listener');
        window.addEventListener('beforeunload', beforeUnloadHandler);
    } else {
        console.log('Removing beforeunload listener');
        window.removeEventListener('beforeunload', beforeUnloadHandler);
    }
}

// 文件下载
function downloadFile(fileId) {
    var permission = window.permission || 0; // 从页面获取权限
    if (permission == 2) {
        alert("可能因为你的违规操作，您暂时无法下载文件！");
        return;
    }
    // 使用新的API路径
    window.location.href = "/file/download/" + fileId;
}

// 文件删除
function deleteFile(fileId) {
    if (!confirm('确定要删除这个文件吗？')) {
        return;
    }
    
    var user = getCurrentUser();
    if (!user) return;
    
    $.ajax({
        url: '/file/delete/' + fileId + '?userId=' + user.userId,
        type: 'DELETE',
        success: function(response) {
            if (response.success) {
                alert('文件删除成功！');
                location.reload(); // 刷新页面
            } else {
                alert('删除失败：' + (response.message || '未知错误'));
            }
        },
        error: function() {
            alert('网络错误，删除失败！');
        }
    });
}

// 文件重命名
function renameFile(fileId, newName) {
    var user = getCurrentUser();
    if (!user) return;
    
    $.ajax({
        url: '/file/rename',
        type: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify({
            fileId: fileId,
            newName: newName,
            userId: user.userId
        }),
        success: function(response) {
            if (response.success) {
                alert('重命名成功！');
                location.reload();
            } else {
                alert('重命名失败：' + (response.message || '未知错误'));
            }
        },
        error: function() {
            alert('网络错误，重命名失败！');
        }
    });
}

// 文件上传
function uploadFile(formData, parentFolderId) {
    var user = getCurrentUser();
    if (!user) return;
    
    // 设置上传开始状态
    onUploadStart();
    
    formData.append('userId', user.userId);
    if (parentFolderId) {
        formData.append('parentFolderId', parentFolderId);
    }
    
    $.ajax({
        url: '/file/upload',
        type: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        success: function(response) {
            // 设置上传完成状态
            onUploadComplete();
            
            if (response.success) {
                alert('上传成功！');
                location.reload();
            } else {
                alert('上传失败：' + (response.message || '未知错误'));
            }
        },
        error: function() {
            // 设置上传完成状态
            onUploadComplete();
            alert('网络错误，上传失败！');
        }
    });
}

// 获取用户文件列表
function getUserFiles(userId, parentFolderId, callback) {
    $.ajax({
        url: '/file/list?userId=' + userId + '&parentFolderId=' + (parentFolderId || 0),
        type: 'GET',
        success: function(response) {
            if (response.success && callback) {
                callback(response.files);
            }
        },
        error: function() {
            console.error('获取文件列表失败');
        }
    });
}

// 按类型获取文件
function getFilesByType(userId, type, callback) {
    $.ajax({
        url: '/file/list/type/' + type + '?userId=' + userId,
        type: 'GET',
        success: function(response) {
            if (response.success && callback) {
                callback(response.files);
            }
        },
        error: function() {
            console.error('获取文件列表失败');
        }
    });
}

// 搜索文件
function searchFiles(userId, keyword, callback) {
    $.ajax({
        url: '/file/search?userId=' + userId + '&keyword=' + encodeURIComponent(keyword),
        type: 'GET',
        success: function(response) {
            if (response.success && callback) {
                callback(response.files);
            }
        },
        error: function() {
            console.error('搜索文件失败');
        }
    });
}

// 获取文件仓库信息
function getFileStore(userId, callback) {
    $.ajax({
        url: '/file/store?userId=' + userId,
        type: 'GET',
        success: function(response) {
            if (response.success && callback) {
                callback(response.fileStore, response.usagePercentage);
            }
        },
        error: function() {
            console.error('获取文件仓库信息失败');
        }
    });
}

// 文件上传开始
function onUploadStart() {
    setPageState('isUploading', true);
}

// 文件上传完成
function onUploadComplete() {
    setPageState('isUploading', false);
}

// 标记页面有未保存的更改
function markUnsavedChanges() {
    setPageState('hasUnsavedChanges', true);
}

// 清除未保存的更改标记
function clearUnsavedChanges() {
    setPageState('hasUnsavedChanges', false);
}

// 清除所有页面状态（用于页面跳转前）
function clearAllPageStates() {
    pageState.isUploading = false;
    pageState.hasUnsavedChanges = false;
    updateBeforeUnload();
}

// 页面初始化
$(document).ready(function() {
    // 监听表单变化
    $('form input, form textarea, form select').on('change input', function() {
        markUnsavedChanges();
    });
    
    // 监听表单提交
    $('form').on('submit', function() {
        clearUnsavedChanges();
    });
    
    // 监听所有链接点击（包括导航栏链接）
    $(document).on('click', 'a[href]', function(e) {
        var href = $(this).attr('href');
        
        // 忽略锚点链接和JavaScript链接
        if (href === '#' || href.startsWith('javascript:')) {
            return;
        }
        
        // 如果有未保存的更改且不在上传状态，弹出确认对话框
        if (pageState.hasUnsavedChanges && !pageState.isUploading) {
            if (!confirm('您有未保存的更改，确定要离开此页面吗？')) {
                e.preventDefault();
                return false;
            }
        }
        
        // 清除所有页面状态
        clearAllPageStates();
    });
    
    // 监听页面刷新和跳转
    $(window).on('beforeunload', function() {
        // 如果不是在上传状态，则清除所有状态
        if (!pageState.isUploading) {
            clearAllPageStates();
        }
    });
    
    // 监听页面卸载事件，确保状态完全清除
    $(window).on('unload', function() {
        clearAllPageStates();
    });
});

// 为了兼容现有代码，保持原函数名
window.download = downloadFile;
window.deleteFile = deleteFile; 