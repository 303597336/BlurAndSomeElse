package com.testdemo.testPictureSelect.imageLoader

import android.content.Context
import java.util.*

/**
 * 媒体处理类（对扫描出来的图片、视频做对应聚类处理）
 * Create by: chenWei.li
 * Date: 2019/1/22
 * Time: 1:17 AM
 * Email: lichenwei.me@foxmail.com
 */
object MediaHandler {
    const val ALL_MEDIA_FOLDER = -1 //全部媒体
    const val ALL_VIDEO_FOLDER = -2 //全部视频
    /**
     * 对查询到的图片进行聚类（相册分类）
     *
     * @param context
     * @param imageFileList
     * @return
     */
    /*@JvmStatic
    fun getImageFolder(context: Context?, imageFileList: ArrayList<ChatPictureBean>?): List<MediaFolder?> {
        return getMediaFolder(context, imageFileList, null)
    }

    */
    /**
     * 对查询到的视频进行聚类（相册分类）
     *
     * @param context
     * @param imageFileList
     * @return
     *//*
    fun getVideoFolder(context: Context?, imageFileList: ArrayList<ChatPictureBean>?): List<MediaFolder?> {
        return getMediaFolder(context, null, imageFileList)
    }*/

    /**
     * 对查询到的图片和视频进行聚类（相册分类）
     *
     * @param context
     * @param imageFileList
     * @param videoFileList
     * @return
     */
    fun getMediaFolder(context: Context?, imageFileList: ArrayList<ChatPictureBean>?, videoFileList: ArrayList<ChatPictureBean>?): List<MediaFolder> {
        val list = ArrayList<ChatPictureBean>()
        imageFileList?.let { list.addAll(it) }
        videoFileList?.let { list.addAll(it) }
        return getMediaFolder(context, list).values.toList()
    }

    fun getMediaFolder22(context: Context?, imageFileList: ArrayList<ChatPictureBean>?, videoFileList: ArrayList<ChatPictureBean>?): Map<Int, Array<MediaFolder?>> {
        val imageFolderMap = imageFileList?.let { getMediaFolder(context, it) }//?.takeIf { it.isEmpty() }
        val videoFolderMap = videoFileList?.let { getMediaFolder(context, it) }

        val fillDataToMap = { folderId: Int, mediaFolder: MediaFolder, allFolderMap: MutableMap<Int, Array<MediaFolder?>>, index: Int ->
            var mediaArray = allFolderMap[folderId]
            if (mediaArray == null) {
                mediaArray = arrayOf(null, null)
                allFolderMap[folderId] = mediaArray
            }
            mediaArray[index] = mediaFolder
        }

        val folderMediaMap = mutableMapOf<Int, Array<MediaFolder?>>()
        videoFolderMap?.forEach { fillDataToMap(it.key, it.value, folderMediaMap, 0) }
        imageFolderMap?.forEach { fillDataToMap(it.key, it.value, folderMediaMap, 1) }

        return folderMediaMap
    }

    //根据媒体所在文件夹Id进行聚类（相册）
    private fun getMediaFolder(context: Context?, fileList: ArrayList<ChatPictureBean>): Map<Int, MediaFolder> {
//        val mediaFolderMap: MutableMap<Int, MediaFolder> = HashMap()
        val mediaFolderMap = HashMap<Int, MediaFolder>()

        //全部
        if (fileList.isNotEmpty()) {
            val pictureBean = fileList[0] //todo greyson 是否需要经过排序再获取第一个媒体文件来作为目录封面
            mediaFolderMap[ALL_MEDIA_FOLDER] = MediaFolder(ALL_MEDIA_FOLDER, "All picture", pictureBean.path, pictureBean.type, fileList)

            //对图片进行文件夹分类, 添加其他的图片文件夹
            fileList.forEach { file ->
                val folderId = file.folderId
                var mediaFolder = mediaFolderMap[folderId]

                if (mediaFolder == null) {
                    mediaFolder = MediaFolder(folderId, file.folderName, file.path, file.type, ArrayList())
                    mediaFolderMap[folderId] = mediaFolder
                }
                mediaFolder.mediaFileList.add(file)
            }
        }
        return mediaFolderMap

        //整理聚类数据
        /*val mediaFolderList: MutableList<MediaFolder> = ArrayList()
        for (folderId in mediaFolderMap.keys) {
            mediaFolderMap[folderId]?.let{mediaFolderList.add(it)}
        }*/

        //按照图片文件夹的数量排序
        /*mediaFolderList.sortWith(Comparator {o1, o2 ->
            when {
                o1.mediaFileList.size > o2.mediaFileList.size -> {
                    -1
                }
                o1.mediaFileList.size < o2.mediaFileList.size -> {
                    1
                }
                else -> {
                    0
                }
            }
        })*/
//        return mediaFolderList
    }
}