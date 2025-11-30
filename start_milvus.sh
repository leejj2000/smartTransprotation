#!/bin/bash

# 启动 Milvus 向量数据库
echo "正在启动 Milvus..."
docker run -d --name milvus-standalone -p 19530:19530 -p 9091:9091 milvusdb/milvus:v2.3.10 \
    milvus run standalone

# 等待 Milvus 启动
echo "等待 Milvus 启动..."
sleep 30

# 检查 Milvus 是否启动成功
if docker ps | grep -q milvus-standalone; then
    echo "Milvus 启动成功!"
else
    echo "Milvus 启动失败!"
    exit 1
fi
