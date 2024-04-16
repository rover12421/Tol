package com.rover12421.tol.util

class MavenLoaderHelper {
    private val repo = Maven.configureResolver().withMavenCentralRepo(true)
        .withRemoteRepo("huaweicloud", "https://mirrors.huaweicloud.com/repository/maven/", "default")
        .withRemoteRepo("aliyun", "http://maven.aliyun.com/nexus/content/groups/public/", "default")
        .withRemoteRepo("sonatype", "https://oss.sonatype.org/service/local/repositories/releases/content/", "default")
        .withRemoteRepo("jitpack", "https://jitpack.io", "default")
}