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
            if (response.success) {
                alert('上传成功！');
                location.reload();
            } else {
                alert('上传失败：' + (response.message || '未知错误'));
            }
        },
        error: function() {
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

// 为了兼容现有代码，保持原函数名
window.download = downloadFile;
window.deleteFile = deleteFile; 