### SimpleReader是一款RSS聚合阅读器

#### 实现功能
- 离线缓存
- 异步图片加载
- 文章收藏
- 夜间模式
- 首页壁纸切换
- 整合科大讯飞语音SDK
- 整合友盟分享及评论SDK
 
#### 依赖库
- 下拉刷新：[地址](https://github.com/chrisbanes/Android-PullToRefresh)
- 科大讯飞
- 友盟社区组件

### Android Studio项目的更新
- 请直接检出as分支

# 从eclipse导入到android studio说明
- png图片的错误(libpng error: Not a PNG file):由于home_bg_0.png本身是jpeg格式,但后缀却是.png.
- PullToRefresh库的使用,为项目新建module,选择eclipse adt项目.
- CacheManager不能使用问题,[看这位童鞋的说明](https://github.com/matrixcloud/SimpleReader/issues/2).

