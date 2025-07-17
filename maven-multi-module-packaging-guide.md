# IntelliJ IDEA中Maven父子模块工程快速打包指南

## 1. 项目结构示例

```
parent-project/
├── pom.xml (父模块)
├── module-common/
│   └── pom.xml
├── module-service/
│   └── pom.xml (依赖module-common)
└── module-web/
    └── pom.xml (依赖module-service和module-common)
```

## 2. 父模块pom.xml配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.example</groupId>
    <artifactId>parent-project</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>
    
    <!-- 子模块列表 - 按依赖顺序排列 -->
    <modules>
        <module>module-common</module>
        <module>module-service</module>
        <module>module-web</module>
    </modules>
    
    <!-- 统一版本管理 -->
    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    
    <!-- 依赖版本管理 -->
    <dependencyManagement>
        <dependencies>
            <!-- 内部模块版本统一 -->
            <dependency>
                <groupId>com.example</groupId>
                <artifactId>module-common</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>com.example</groupId>
                <artifactId>module-service</artifactId>
                <version>${project.version}</version>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

## 3. 子模块依赖配置示例

### module-service/pom.xml
```xml
<project>
    <parent>
        <groupId>com.example</groupId>
        <artifactId>parent-project</artifactId>
        <version>1.0.0</version>
    </parent>
    
    <artifactId>module-service</artifactId>
    
    <dependencies>
        <dependency>
            <groupId>com.example</groupId>
            <artifactId>module-common</artifactId>
        </dependency>
    </dependencies>
</project>
```

## 4. IDEA中的快速打包方法

### 方法一：使用Maven窗口（推荐）

1. **打开Maven窗口**
   - 点击右侧的"Maven"标签页
   - 或者使用快捷键：`View` → `Tool Windows` → `Maven`

2. **执行打包命令**
   ```
   # 在父项目上右键选择以下任一命令：
   - clean compile    # 清理并编译
   - clean package    # 清理并打包
   - clean install    # 清理、打包并安装到本地仓库
   ```

3. **具体操作步骤**
   - 在Maven窗口中找到父项目
   - 展开"Lifecycle"节点
   - 依次双击：`clean` → `install`
   - 或者右键父项目名称 → `Run Maven Build`

### 方法二：使用运行配置

1. **创建Maven运行配置**
   - `Run` → `Edit Configurations...`
   - 点击"+"号 → 选择"Maven"
   - 配置如下：
     ```
     Name: Multi-Module Package
     Command line: clean install
     Working directory: [父项目根目录]
     ```

2. **执行配置**
   - 点击运行按钮或使用快捷键`Shift + F10`

### 方法三：使用Terminal

1. **打开Terminal**
   - `View` → `Tool Windows` → `Terminal`
   - 或快捷键：`Alt + F12`

2. **执行Maven命令**
   ```bash
   # 进入父项目目录
   cd /path/to/parent-project
   
   # 清理并安装所有模块
   mvn clean install
   
   # 跳过测试的快速打包
   mvn clean install -DskipTests
   
   # 并行构建（加速）
   mvn clean install -T 4
   ```

## 5. 处理模块间依赖的关键点

### 5.1 模块构建顺序
- Maven会自动根据依赖关系确定构建顺序
- 在父pom.xml中的`<modules>`顺序最好按依赖关系排列
- 被依赖的模块会先构建

### 5.2 版本一致性
```xml
<!-- 使用${project.version}确保版本一致 -->
<dependency>
    <groupId>com.example</groupId>
    <artifactId>module-common</artifactId>
    <version>${project.version}</version>
</dependency>
```

### 5.3 本地仓库安装
- 使用`mvn install`而不是`mvn package`
- `install`会将jar包安装到本地Maven仓库
- 其他模块才能正确引用依赖

## 6. 常用打包命令对比

| 命令 | 说明 | 适用场景 |
|------|------|----------|
| `mvn compile` | 只编译源代码 | 快速检查编译错误 |
| `mvn package` | 编译并打包为jar/war | 生成可分发的包 |
| `mvn install` | 打包并安装到本地仓库 | 模块间有依赖关系 |
| `mvn deploy` | 安装到远程仓库 | 发布到私服或中央仓库 |

## 7. 优化技巧

### 7.1 并行构建
```bash
# 使用4个线程并行构建
mvn clean install -T 4

# 使用1C表示每个CPU核心一个线程
mvn clean install -T 1C
```

### 7.2 跳过测试
```bash
# 跳过测试执行但编译测试代码
mvn clean install -DskipTests

# 完全跳过测试
mvn clean install -Dmaven.test.skip=true
```

### 7.3 离线模式
```bash
# 不检查远程仓库更新
mvn clean install -o
```

### 7.4 指定Profile
```bash
# 激活特定的构建profile
mvn clean install -P production
```

## 8. 常见问题及解决方案

### 8.1 依赖找不到
**问题**：`Could not resolve dependencies`
**解决**：
1. 确保先执行了被依赖模块的`install`
2. 检查版本号是否一致
3. 清理本地仓库：删除`~/.m2/repository`中对应目录

### 8.2 循环依赖
**问题**：模块A依赖B，B又依赖A
**解决**：
1. 重新设计模块结构
2. 提取公共部分到新模块
3. 使用接口分离依赖

### 8.3 构建顺序错误
**问题**：被依赖的模块后构建
**解决**：
1. 调整父pom.xml中`<modules>`的顺序
2. 或让Maven自动处理依赖顺序

## 9. IDEA特定设置

### 9.1 自动导入
- `File` → `Settings` → `Build` → `Build Tools` → `Maven`
- 勾选"Import Maven projects automatically"

### 9.2 委托构建给Maven
- `File` → `Settings` → `Build` → `Build Tools` → `Maven` → `Runner`
- 勾选"Delegate IDE build/run actions to Maven"

### 9.3 Maven Home设置
- 确保使用正确的Maven版本
- `File` → `Settings` → `Build` → `Build Tools` → `Maven`
- 设置正确的"Maven home directory"

## 10. 快捷操作总结

1. **最快速打包**：在Maven窗口双击父项目的`install`
2. **带清理的打包**：先双击`clean`，再双击`install`
3. **跳过测试打包**：Terminal中执行`mvn clean install -DskipTests`
4. **并行快速打包**：`mvn clean install -T 4 -DskipTests`

通过以上方法，您可以在IntelliJ IDEA中高效地处理Maven多模块项目的打包，即使存在复杂的模块间依赖关系也能顺利完成构建。