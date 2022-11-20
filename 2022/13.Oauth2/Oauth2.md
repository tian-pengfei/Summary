# OAuth2协议详解与应用场景

[TOC]

## 简介

通过本文我们会将了解Oauth2授权协议的在不同场景下的应用流程。



## 场景（引入OAuth2）

现在我们有这样一个场景，如下图所示。用户在使用A应用的时候，有些功能不得不使用到用户在B应用的上的资源，这时候我们就不得不需要A应用帮我们去获取用户在B应用的上数据。

![image-20221118105616881](image/image-20221118105616881.png)



这样场景很多，

+ 比如我们一些网站可使用github账户直接登录，这时候我们就不得不访问用户在github上的用户资源了。

+ 对于我们的一般项目可以看作成了两个应用，客户端（包括**单页应用**）和服务端，客户端需要用户在服务端上的资源也是符合这种场景的。



在OAuth出现之前，我们是怎么做的呢？

用户不得不把在B应用上的长期凭证（eg:用户名，密码），交给我们的A应用。然后A应用拿着用户的长期凭证，去和B交互获取用户在B上面的资源。



这么做有哪些问题？

+ 权限过大：A应用拿到用户的密码，就可以拥有用户的所有权限。甚至直接修改用户的密码，这显然A应用拥有了过多的用户在B应用上的权限
+ 难以撤销或过期：一旦A应用拿到用户名密码，只要用户不修改密码，A应用一直拥有在用户在B应用上的权限，除非修改密码。对于这种密码的长期凭证，用户一般是很少修改的



随着互联网发展，各大厂商意识到上面的方式有诸多不足，后来都做自己的授权流程防止这种问题的发生。为了统一标准出现了OAuth1协议，由于协议复杂和困惑的地方较多，有起草发布一份新的Oauth2协议，随后Oauth2这种**`授权协议`**也逐渐流行了起来。





## Oauth2.0简要说明



### 角色

现在我们把上面的场景图进行分解，分解为下面几种角色

![image-20221118113625506](image/image-20221118113625506.png)



+ **资源拥有者（resource owner）**

  资源的拥有者，在大多是情况下是我们使用终端的用户，也就是上面场景中的用户。

+ **资源服务器（resource server）**

  持有用户的受保护的资源，这些资源可以用过`access tokens`拿到。

+ **客户端（client）**

  帮助**资源拥有者（resource owner）**拿到它在**资源服务器（resource server）**上的受保护的资源，并可能做一些处理。当然此角色是拿到了**资源拥有者（resource owner）**授权的。

+ **授权服务器（authorization server）**

  此角色的任务是颁发可以访问**资源服务器（resource server）**的**access token**，这Token让Client真正得到了可以访问**资源服务器（resource server）**上资源的权限。

  但是前提是校验通过授权的资料（eg： 用户密码）。



> 注意：这些都是Oauth2授权流程中的角色，不要受其他的实体特性影响。可能某个实体在场景一下扮演角色1，在场景2下扮演角色2。还有就是某个实体还有可能同时扮演两种角色，比如授权服务器和资源服务器有时候就是一个实体。



### 高层抽象授权流程

我们现在了解了在Oauth2的授权过程中有了那些角色，我们现在在分析一下这些角色是怎么交互。我们首先提一下是，Oauth2针对不同的情况对授权过程的实现是略微不同的，但是我们站在抽象的高层来说整体过程大致如下图，可以看作是接口和实现的关系。

```tex

     +--------+                               +---------------+
     |        |--(A)- Authorization Request ->|   Resource    |
     |        |                               |     Owner     |
     |        |<-(B)-- Authorization Grant ---|               |
     |        |                               +---------------+
     |        |
     |        |                               +---------------+
     |        |--(C)-- Authorization Grant -->| Authorization |
     | Client |                               |     Server    |
     |        |<-(D)----- Access Token -------|               |
     |        |                               +---------------+
     |        |
     |        |                               +---------------+
     |        |--(E)----- Access Token ------>|    Resource   |
     |        |                               |     Server    |
     |        |<-(F)--- Protected Resource ---|               |
     +--------+                               +---------------+
```

上面是我们协议中的给出的图，我们按照这个图在赋予到上面我们的角色图。如下

![image-20221118143338665](image/image-20221118143338665.png)



我们来依次解释一下我们的抽象流程图。

+ (A)：  客户端**（Client）**向用户（**ResourceOwner**）请求授权。

  这个实现一般有多种，我现在先简单了解一下，有的是**Client**直接向**ResourceOwner**请求。或者是通过 **authorization server**间接的获取**ResourceOwner**的授权。现在知道有多种即可。后面会详细介绍。

  

+ (B)： 客户端(**Client**)获取授权，表示**用户ResourceOwner**同意授予访问的权限。

  怎么表示客户端获已经取授权呢？**Client**会获得一个数据，这个数据就表示Client已经获取响应的授权。不同类型的授权流程，这个此数据会有所不同。

  

+ (C)：现在客户端Client会向我们授权服务器提交资源拥有者已经授权的相关证明，还会提交客户端Client自己的身份。

  客户端身份？再授权流程之前客户端Client会把自己的注册到授权服务中，表示自己支持对B应用的授权访问。

  

+ (D):  授权服务器首先会对 提交的授权证明和客户端的身份进行校验，若校验通过，则会签发一个`access token`  ，提交给客户端Client

   

  ----------------------------------------------以上为授权流程，下面为E、F为授权后的访问流程--------------------------------------------------

+ (E)：客户端Client会携带AccessToken访问ResourceServer上的属于用户（**ResourceOwner**）的资源

   

+ (F): B应用中的资源服务器进行校验，Token校验通过，返回Client请求的资源



> 注意：上面两个实体之间的箭头指向，表示是两个实体采用某种方式进行交互。我们并不能想当然认为就是某一种方式（eg:一个实体直接发Http请求另一个）。就拿A步骤和B步骤来说，它可能是Client通过用户使用的浏览器转发到授权服务器，然后在授权服务器上用户提交同意授权然后以重定向的方式把表示用户同意授权的数据在提交给Client。很明显这种方式就比较复杂了。这就是为什么说上面的流程图只是一个抽象的流程图。后面我们会详细描述不同的实现。



### 不同场景下角色分析

Oauth2协议下的角色，适应很多不同的场景，其实本质就是我们一开始提到的场景。主要是以区分不同类型的Client



#### 服务端程序（Server-Side apps）

这种类型主要是针对运行在远程服务器的程序，类似于网站的后台，某些客户端的后台程序。这种类型的程序保密性较好，持有数据不会暴漏于公共。

+ **resource owner**:  在终端进行操作的用户
+ **client**： 服务器的程序也就是**Server-Side apps**
+ **resource server**：拥有受保护的用户资源的服务
+ **authorization server**：颁发**client**访问**resource server**权限的服务



#### 单页程序（Single-Page Apps）

也称之为 **browser-based apps**，此种类型一般会把程序的源码会从从远端下载，然后运行在浏览器上。现在我们大部分的网站开发的前端都是以这种形式进行开发的运行的。这种程序有一个特点就是程序暴漏在了公共区域，无法保证持有保密性较高的数据的安全性。

+ **resource owner**:  在终端进行操作的用户，也就是在浏览器上操作的用户
+ **client**： 运行在浏览器的前端程序
+ **resource server**：拥有受保护用户资源的服务。对应后台程序的一些服务
+ **authorization server**：颁发**client**访问**resource server**权限的服务，对应后台的授权服务



#### 移动原生应用（Mobile and Native Apps）

这种应用程序的特点就是在移动设备上，一般指的就是手机上的app。同样手机上的app同意无法持有保密性较高的数据。

+ **resource owner**:  在终端进行操作的用户，操作移动设备的用户。
+ **client**： 运行在移动设备的应用
+ **resource server**：拥有受保护用户资源的服务。对应后台程序的一些服务
+ **authorization server**：颁发**client**访问**resource server**权限的服务，对应后台的授权服务



> 针对后两种应用场景，一个项目下的客户端和服务端也可以带入Oauth2所定义的角色，我之间还见过一些第三方的github移动应用app，这些移动应用是没有自己对应的后端的。也挺契合我们上面`Mobile and Native Apps`所描述的角色分配的

上面就是一些比较常见的应用类型，根据不同实体之间的关系和一些不同业务场景还有不同的应用的对数据保密性的能力的不同，Oauth2提出几种不同的方案。下面我们将会一一详细描述这些方案。



## 客户端注册

在进行流程之前，我们得先把Client进行注册到对应的程序上。

就拿前面的A应用和B应用来说，不是谁想在B应用上申请查看用户的资源就能查看的，首先我们得先把A注册在B的授权相关的服务上的。

我们以B应用作为github为例查看一下都怎么注册？

访问https://github.com/settings/apps/new，你会看到一个需要填写信息的页面，这时候我们填写相关的A应用的信息就好

![image-20221120162207756](image/image-20221120162207756.png)



Callback URL是授权过程中将会使用到，有些模式用户成功授权后会有回调的通知地址来告诉Client。

信息填写完之后会生成一个`client id` 和`client secret` 这两个参数也将会在后面授权的过程中用到，用来校验client的身份。



## 授权码模式（Authorization Code ）

这种模式对Client提出了要求，要保证`client secret`机密性。我们服务端程序（`Server-Side Apps`）是运行在web服务器的，可以保证`client secret`机密性。此种模式更多的应用在了`Server-Side Apps`上了（在没有PKCE扩展之前）。

### user-agent

​	在我们了解下面的模式之前，我们先了解一个名词**user-agent**，首先我们这里的user-agent是一个程序，这个程序代理我们现实世界里面的人的行为，去和其他程序进行交互。Web中浏览器就是我们最常见的**user-agent**。在本授权模式中，user-agent将会辅助我们实现上面的抽象流程。

​		在本文章中直接将UserAgent直接理解成具有重定向功能的浏览器会更加便于理解。



### 授权流程

我们具体了解一下流程，然后理解优点和好处。

协议图：

```tex
     +----------+
     | Resource |
     |   Owner  |
     |          |
     +----------+
          ^
          |
         (B)
     +----|-----+          Client Identifier      +---------------+
     |         -+----(A)-- & Redirection URI ---->|               |
     |  User-   |                                 | Authorization |
     |  Agent  -+----(B)-- User authenticates --->|     Server    |
     |          |                                 |               |
     |         -+----(C)-- Authorization Code ---<|               |
     +-|----|---+                                 +---------------+
       |    |                                         ^      v
      (A)  (C)                                        |      |
       |    |                                         |      |
       ^    v                                         |      |
     +---------+                                      |      |
     |         |>---(D)-- Authorization Code ---------'      |
     |  Client |          & Redirection URI                  |
     |         |                                             |
     |         |<---(E)----- Access Token -------------------'
     +---------+       (w/ Optional Refresh Token)
```





![image-20221118175011841](image/image-20221118175011841.png)



+ (A)： A步骤主要是Client来请求用户授予访问的权限。

  A1:当Client发现需要获得权限来获取用户在其他地方的资源时，便告诉向浏览器重定向到授权服务器进行访问。

  A2: Client告诉浏览器重定向的网址,例如：

  ```tex
  https://B.com/authorization-server/oauth/authorize
  ?client_id=mRkZGFjM
  &response_type=code
  &state=5ca75bd30
  &redirect_uri=https//A.com/cb
  &scope=photos
  ```

  + 先说`redirect_uri`，这个UR其实是授权服务器校验通过用户的提交的信息后（B），让浏览器重定向(C)时用到网址。

  + `response_type` 表明返回的是`code`，这也就是授权码

  + `state`， 发送给授权服务器，授权服务器原封不动的，会在后面重定向（C）时附加到`redirect_uri`。一般两个作用。

    + 可以表明，授权成功后Client要重定向的位置。
    + 或者每次请求是随机的一个值，这样可以来作为CSRF保护。

  + `client_id` client的标识，之前注册在B的授权服务器中的

  + `scope` 表示想要申请的权限的范围

    

    

+  （B）:现在授权服务器会通过浏览器，让用户提交凭证来表示统一给Client授权。

  ​	B1：一般授权服务器会给出一个界面，让用户提交凭证信息

  ​	B2:用户同意，然后填写信息完毕后，提交凭证信息给授权服务器

  

+ (C)： 授权服务器拿到用户提交的凭证信息并验证成功后，告诉浏览器重定向到`redirect_uri`，并携带生成的参数`authorization code`和A步骤中的参数state（原封不动返回）。利用了浏览器的重定向功能告诉了A的授权码。

  eg:

  ```tex
  https://A.com/cb?code=Yzk5ZDczMzRlNDEwY&state=5ca75bd30
  ```

  

+ (D)：现在A收到授权服务器的`authorization code`后，并校验state是否和当时一致，就直接会去向授权服务器请求AccessToken，这次请求会携带`authorization code`、出于安全的考虑会携带 C步骤使用的` redirection URI`，授权服务器会对它进行校验。

  ```tex
  POST /oauth/token HTTP/1.1
  Host: authorization-server.com
   
  code=Yzk5ZDczMzRlNDEwY
  &grant_type=code
  &redirect_uri=https://A.com/cb
  &client_id=mRkZGFjM
  &client_secret=ZGVmMjMz
  ```

  

  ​	

+ (E)：收到Client发送来的信息后，校验客户端的id和secret 还有就是校验`redirect_uri`是否和C步骤一致，校验授权码`code`。都校验成功后会返回授权服务器签发的 `access token`、`refresh token`（可选的）。

  

+ （F）：多说一步，获取成功的`access token`,Client会告诉浏览器访问（重定向到）哪一个网址，这个网址和实际业务相关的。这个网址还可能与上面的state值相关，因为可能涉及到不情况的授权重定向不同的页面，这得看使用者是怎么设计的。这就是state其中的一个功能。

  

### 优点

可以说上面的授权码模式比其他模式复杂很多，它的复杂为它的授权过程中安全性做出了保障，他也是最安全的模式。

+ 对客户端进行了身份校验（说明其他模式可能不校验）
+  保证了`access token`机密性，观察上面的流程可知，AccessToken只暴漏给Client和授权服务器。（说明其他模式可能暴漏给其它实体）





## 简化模式（implicit）

这种模式由于安全性的原因已经不再推荐使用，请看[这里](https://oauth.net/2/browser-based-apps/)。但是大家还是简单的了解一下为后面引出基于PKCE的授权码模式（`Authorization Code with PKCE.`）做出铺垫。

它适用于运行在浏览器中的`单页应用（Single-Page Apps/browser-based-apps）`,也就是现在运行在浏览器上的前端应用，承担了抽象流程图中Client的角色。

因为`Single-Page Apps`整个源代码（脚本语言）运行在浏览器上，是向公众开放的。无法保证`client secret`机密性。所以当时就设计出了这种简化模式的方案，是个折中的方案。



### 授权流程

```tex

     +----------+
     | Resource |
     |  Owner   |
     |          |
     +----------+
          ^
          |
         (B)
     +----|-----+          Client Identifier     +---------------+
     |         -+----(A)-- & Redirection URI --->|               |
     |  User-   |                                | Authorization |
     |  Agent  -|----(B)-- User authenticates -->|     Server    |
     |          |                                |               |
     |          |<---(C)--- Redirection URI ----<|               |
     |          |          with Access Token     +---------------+
     |          |            in Fragment
     |          |                                +---------------+
     |          |----(D)--- Redirection URI ---->|   Web-Hosted  |
     |          |          without Fragment      |     Client    |
     |          |                                |    Resource   |
     |     (F)  |<---(E)------- Script ---------<|               |
     |          |                                +---------------+
     +-|--------+
       |    |
      (A)  (G) Access Token
       |    |
       ^    v
     +---------+
     |         |
     |  Client |
     |         |
     +---------+
```





![image-20221119142719867](image/image-20221119142719867.png)

`Web-hosted-Resource`: 就是前端应用源代码资源，存储在相应的服务器上，通过Http直接访问下载到浏览器中运行  。对应服务有Nginx、Tomcat，都可以做源代码资源的服务器，提供下载。



+ AB步骤和上面的授权码模式差不多，只是在A步骤中重定向到授权服务器，对应的URL中参数略微不一样，如下。

  ```tex
  https://b.com/oauth/authorize?
    response_type=token&
    client_id=12345&
    redirect_uri=https://A.com/cb&
    scope=read
  ```

  reponse_type 为Token表示授权服务器直接返回Token

  redirect_uri 为脚本代码资源的下载地址，后面会用到

  

+ (C)：步骤c的话，就是访问授权服务器告诉浏览器重定向的地址（`Fragment`形式携带Token）

  这个重定向的地址是下面格式

  `https://A.com/cb#token=ACCESS_TOKEN`

  

+ (D)：浏览器请求要求重定向的地址是`https://A.com/cb#token=ACCESS_TOKEN`

  这个格式会有一个优点，就是浏览器在请求这个地址的时候不会把Token发送出去，只会请求`https://A.com/cb`。这是利用了锚点（`Fragment`）的特性。

  优点：减少Token的传播，也就减少了暴漏的可能性

+ (E)：从代码服务器下载下来Client源代码

+ (F): 执行A程序也就是Client的源代码，执行源代码会提取Token

+ (G)： A程序获取了Token

  

**缺点**：

+ 没校验Client，因为只发送一个clientId，并不能校验Client，为什么不发送`client secret`?因为Client运行是面向公共的，就直接暴漏了。（Resource Owner 也就是用户可以直接看到，浏览器也可以获取到）
+ 暴漏access token ，Resource Owner 也就是用户可以直接看到，浏览器也可以获取到。

由于上面的缺点，此种模式下的授权安全性并不高，有令牌容易泄露和令牌重放等问题，现在早已不推荐并逐渐废弃掉。

那我们采用哪种模式来针对这种`Single-Page Apps`/`browser-based-apps`的应用场景呢？

基于PKCE的授权码模式`Authorization Code with PKCE`.





## PKCE的授权码模式（Authorization Code with PKCE）

​		PKCE 全称为`Proof Key for Code Exchange by OAuth Public Clients`，是基于`Authorization Code` 的授权模式下扩展协议。起初设计是用来针对原生移动端应用程序`mobile apps`的授权，让`Authorization Code` 的授权模式可以应用在`mobile apps`类型的客户端中，到后来被推荐在`Single-Page Apps`中使用（因为`implicit`授权模式不安全）。现在也推荐使用PKCE 在`Server-Side Apps`原来的`Authorization Code`上扩展使用，防止`authorization code`注入攻击。

现在上面的几种类型都可以使用`Authorization Code`的授权模式。（有的是必须使用PKCE 扩展协议来辅助）。

我们接下来看看什么是`PKCE`？

首先应用在了Authorization Code 模式的协议上了，所以流程是不变的，只是在某些交互上增加了些参数和某些步骤上增加校验。我们依然看`Authorization Code`模式的交互图进行讲解。

![image-20221118175011841](image/image-20221118175011841.png)

​	

### 第一个不同的地方

A步骤中Client应用会告诉浏览器一个重定向的地址，现在在`PKCE`协议扩展下增加了几个参数，来我们看一下Client应用要求浏览器传递的地址

```tex
https://B.com/authorize
?client_id=eKNjzFFjH9A1ysYd
&response_type=code
&redirect_uri=com.a.app://auth
&state=1234zyx
&scope=photos
&code_challenge=hKpKupTM381pE10yfQiorMxXarRKAHRhTfH_xkGf7U4
&code_challenge_method=S256
```

显然比之前多了两个参数`code_challenge`和`code_challenge_method`。

我们看一下这两个参数是怎么生成的出来？

说这两个参数之前，我们先说一下就是`code verifier`，`code_challenge`的生成和它有关

`code verifier`

一个随机的字符串，只能用`A-Z`, `a-z`, `0-9`和 `-._~`组成。长度为43~128。

我们根据上面的`code verifier`使用一种可能去计算获得`code challenge`

> code_challenge =  Base64-URL-encoded（ SHA256（code_verifier））

​		上面的计算就是一种可能，也就是说code_verifier要经过一个方法处理，比如SHA256或者什么也不做的处理方法，然后再把处理的结果使用 Base64-URL-encoded的编码方法上进行编码，最后生成code_challenge 。 其中我们一开始处理方法就是`code_challenge_method`的值，`S256`就表示处理方法是`SHA256`，什么都不做的话`code_challenge_method`的值为`plain`。

​		当授权服务器收到这个请求的时候会发现code_challenge参数，就会意识到是`Authorization Code with PKCE`模式的授权，就会在授权码生成的时候关联`code_challenge`。有两种关联的模式，一种是把`authorization code`和`code_challenge`一块存储到数据库中，另一种是把`code_challenge`编码进`authorization code`中。授权服务器在步骤上不会有任何与之前不同。

​	

### **第二个不同的地方**

第二个不一样的地方放生Client应用申请Token的时候，也就是从步骤C会有些参数不一样。下面为C步骤的请求数据

```tex
POST /oauth/token HTTP/1.1
Host: B.com
 
grant_type=code
&code=Yzk5ZDczMzRlNDEwY
&redirect_uri=a.com://auth
&client_id=eKNjzFFjH9A1ysYd
&code_verifier=Th7UHJdLswIYQxwSg29DbK1a_d9o41uNMTRmuH0PM8zyoMAQ
```

Clien再申请Token的时候会携带上code_verifier。当授权服务器收到这个这个请求的时候，除了会做之前的一些校验，还会进行`code_challenge`的校验。通过授权码查询到之前的`code_challenge`和`code_challenge`的计算方法，然后通过传递来`code_verifier`进行计算，生成新的`code_challenge`，最后进行比对，是否与之前传递来的`code_challenge`一致。校验通过后进行Token的签发，并返回。



> 注意：上面Client请求Token的时候并没有携带**client_secret** ，这是应为client_secret在PKCE下是可选的，当你使用server-side apps 时候你完全可以传递client_secret了，因为server-side apps 类型的app可以保证client_secret的保密性，这也使得授权过程更加安全。client_secret的可选也让在一些不能保证`client_secret`保密性的app上使用`Authorization Code`模式成为了可能性



### 应用场景

此种类型的授权模式针对服务端程序、单页程序、移动原生的程序都比较适用。因为`client_secret`是可选的这就为一些无法保证数据保密性的应用提供可以使用的可能性。



## 密码模式（resource owner password credentials）

接下来的模式都会比较简单，密码模式是在用户（resource owner ）完全信任Client(客户端)的时候适合使用，比如用户会比较信任一些设备上的操作系统或者一些高保密的应用。这种模式用户会把密码凭证交给Client。这些情况下Client和Resource Owner 关系比较紧密。



### 授权流程

```tex
     +----------+
     | Resource |
     |  Owner   |
     |          |
     +----------+
          v
          |    Resource Owner
         (A) Password Credentials
          |
          v
     +---------+                                  +---------------+
     |         |>--(B)---- Resource Owner ------->|               |
     |         |         Password Credentials     | Authorization |
     | Client  |                                  |     Server    |
     |         |<--(C)---- Access Token ---------<|               |
     |         |    (w/ Optional Refresh Token)   |               |
     +---------+                                  +---------------+
```



![image-20221120144609267](image/image-20221120144609267.png)



+ A：当需要访问用户在其他应用的资源的时候，Client会索要用户的**密码凭证**。

+ B: Client会直接拿着用户的密码凭证去去向B应用的授权服务器申请Token

```tex
	 POST /token HTTP/1.1
     Host: B.com
     Authorization: Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW
     Content-Type: application/x-www-form-urlencoded
     grant_type=password&username=johndoe&password=A3ddj3w

```

​	`grant_type` 为`password`表示为此授权模式为Oauth2的密码模式。



C: 授权服务校验密码凭证通过后会签发Token响应给Client（A应用）。

> 注意：这种模式就和Oauth2出现之前大家采用的方案类似。直接把凭证交给了Client。
>
> Oauth2针对这种方案要求Client使用完用户的密码凭证必须清除。



## 客户端模式（client credentials）



### 授权流程

```tex
     +---------+                                  +---------------+
     |         |                                  |               |
     |         |>--(A)- Client Authentication --->| Authorization |
     | Client  |                                  |     Server    |
     |         |<--(B)---- Access Token ---------<|               |
     |         |                                  |               |
     +---------+                                  +---------------+
```



![image-20221120151435530](image/image-20221120151435530.png)



+ A: 步骤Client会直接请求授权服务器，携带客户端自己的一些凭证（`client_id`、`client_secret`）



```javascript
 
  https://b.com/token?
  grant_type=client_credentials&
  client_id=xxxxxxxxxx&
  client_secret=xxxxxxxxxx
```

+ B: 校验过后Client的身份后会签`access token`



> 此流程中，resource owner是谁？resource owner 就是 client的自己，承担了两个角色。



## EndPoint

​		在我们协议交互的过程中，涉及到一些`EndPoint`，也就是我们协议要求我们有一些Http接口去提供。现在我们来过一下各个`EndPoint`，总共接口有三个，在有些模式下有些接口是用不到。`Authorization Code`模式是把所有的`EndPoint`用到了，我们以此模式为例看一下`EndPoint`。



![image-20221118175011841](image/image-20221118175011841.png)

### Authorization endpoint 

此`Endpoint`为授权服务器所提供，上图中步骤A2是在访问此`endpoint`，`Authorization endpoint` 是被用来联系资源所有者和校验资源所有者提供的凭证。

通常在authorization code模式和 implicit 授权模式下使用



### Redirection endpoint

此`EndPoint`为Client角色的实体提供，是授权服务器引导浏览器重定向到Client的`EndPoint`。为上图步骤D所示。



### Token endpoint

此`EndPoint`为授权服务器所提供，是用来获取Token的，除了`implicit` 授权模式直接会从`Authorization endpoint` 获取Token，其他的授权模式都会从此endPoint获取Token。



## Token



### Access Token

`Access Token`为访问受保护资源的凭证，被`resource Owner`授权，由授权服务器签发。

它是一个对Client不透明的字符串，它对Resource Server代表着访问范围和有效时长。

`Access token`基于需要，可以有不同的格式和规则。

### Refresh Token

`Access Token`过期之后我们必须要再走一遍之前的复杂流程吗？不需要的。在一些模式下是可以更使用`Refresh Token`更新我们的`Access Token`。

我们在一些模式中，在获取`Access Token`时候可以可选的获取`Refresh Token`。

```tex
  +--------+                                           +---------------+
  |        |--(A)------- Authorization Grant --------->|               |
  |        |                                           |               |
  |        |<-(B)----------- Access Token -------------|               |
  |        |               & Refresh Token             |               |
  |        |                                           |               |
  |        |                            +----------+   |               |
  |        |--(C)---- Access Token ---->|          |   |               |
  |        |                            |          |   |               |
  |        |<-(D)- Protected Resource --| Resource |   | Authorization |
  | Client |                            |  Server  |   |     Server    |
  |        |--(E)---- Access Token ---->|          |   |               |
  |        |                            |          |   |               |
  |        |<-(F)- Invalid Token Error -|          |   |               |
  |        |                            +----------+   |               |
  |        |                                           |               |
  |        |--(G)----------- Refresh Token ----------->|               |
  |        |                                           |               |
  |        |<-(H)----------- Access Token -------------|               |
  +--------+           & Optional Refresh Token        +---------------+ 
```

上图的步骤AB就是我们抽象的授权流程，步骤B中在获取Access Token 的时候我们同样获取了Refresh Token。当Token过期的时候我们利用`Refresh Token`去更新我们的Token，很显然`Refresh Token` 的有效时间比Access Token要长。访问的`EndPoint`为`Token EndPoint`



## 总结

​		我们一开始面对的问题是A软件要访问B软件上用户的数据的时候，怎么才能更好更安全的实现呢？传统方式存储直接存储用户在另一个软件上的凭证，这样会有很多的问题，不太安全。这时候我们出现Oauth2**授权**协议，来定义我们授权的交互流程。

​		Oauth2首先办公品我们定义了不同角色，`resource owner`、`resource server`、`client`、`authorization server`，而后我们通过抽象的流程图了解了这些角色在授权的过程中分别要做了些什么。

​		我们了解了我们现在需要授权的程序大致都是什么类型。大致分为Server-Side Apps 、Single-Page Apps 还有Mobile and Native  Apps。不同类型的App会有不同的能力，根据这些能力和一些实体之间的业务关系。Oauth2协议将会分为几种模式：`Authorization Code`、`implicit`、`resource owner credentials`、 `client credentials`授权模式。这些模式安全性也不尽相同，其中`Authorization Code`是最安全的。因为有些模式不够安全，为了增加授权过程的安全性。出现了PKCE扩展协议，可以让一些无法保存一些保密数据的应用使用`Authorization Code`模式，放弃使用不安全的`implicit`模式来增加授权过程中安全性。

​		我们还了解再授权过程中需要那些EndPoint：`Authorization Endpoint`、`Redirection Endpoint`、`Token Endpoint`。我们还知道了Access Token过期了使用Refresh Token进行我们的Token的更新。

![Oauth2](image/Oauth2.png)

## 参考

https://www.oauth.com/ 

https://www.rfc-editor.org/rfc/rfc6749
