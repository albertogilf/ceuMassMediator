<#macro header component="" >
    <?xml version='1.0' encoding='UTF-8' ?>
    <!DOCTYPE html>
    <html xmlns="http://www.w3.org/1999/xhtml">
        <head id="j_idt2"><link type="text/css" rel="stylesheet" href="/mediator/faces/javax.faces.resource/theme.css.xhtml?ln=primefaces-aristo" />
            <script type="text/javascript" src="/mediator/faces/javax.faces.resource/jsf.js.xhtml?ln=javax.faces&amp;stage=Development"></script>
            <link type="text/css" rel="stylesheet" href="/mediator/faces/javax.faces.resource/components.css.xhtml?ln=primefaces&amp;v=6.1" />
            <script type="text/javascript" src="/mediator/faces/javax.faces.resource/jquery/jquery.js.xhtml?ln=primefaces&amp;v=6.1"></script>
            <script type="text/javascript" src="/mediator/faces/javax.faces.resource/core.js.xhtml?ln=primefaces&amp;v=6.1"></script>
            <script type="text/javascript" src="/mediator/faces/javax.faces.resource/components.js.xhtml?ln=primefaces&amp;v=6.1"></script>
            <script type="text/javascript" src="/mediator/faces/javax.faces.resource/jquery/jquery-plugins.js.xhtml?ln=primefaces&amp;v=6.1"></script>
            <script type="text/javascript">if (window.PrimeFaces) {
                    PrimeFaces.settings.locale = 'es_ES';
                    PrimeFaces.settings.projectStage = 'Development';
                }</script>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
            <meta http-equiv="pragma" content="no-cache" />
            <meta http-equiv="cache-control" content="no-cache" />
            <meta name="viewport" content="width=device-width, initial-scale=1" />
            <meta name="author" content="Alberto Gil de la Fuente" />
            <!--[if lte IE 8]&gt;&lt;script src="assets/js/ie/html5shiv.js"&gt;&lt;/script&gt;&lt;![endif]-->
            <link rel="stylesheet" href="/mediator/assets/css/main.css" />
            <script src="/mediator/assets/js/ceumm.js"></script>
            <link rel="stylesheet" href="/mediator/assets/css/templates.css" />
            <!--[if lte IE 8]&gt;&lt;link rel="stylesheet" href="assets/css/ie8.css" /&gt;&lt;![endif]-->
            <script>
            (function (i, s, o, g, r, a, m) {
                i['GoogleAnalyticsObject'] = r;
                i[r] = i[r] || function () {
                    (i[r].q = i[r].q || []).push(arguments)
                }, i[r].l = 1 * new Date();
                a = s.createElement(o),
                        m = s.getElementsByTagName(o)[0];
                a.async = 1;
                a.src = g;
                m.parentNode.insertBefore(a, m)
            })(window, document, 'script', 'https://www.google-analytics.com/analytics.js', 'ga');

            ga('create', 'UA-37347042-1', 'auto');
            ga('send', 'pageview');

            </script>
            <link rel="shortcut icon" href="http://ceumass.eps.uspceu.es/favicon.ico" />
            <#if (component)?has_content>
                <title>CEU mass mediator - ${component.getTitle()} details</title>
            <#else>
                <title>CEU mass mediator - Error!!!</title>
            </#if>

        </head>

        <body id="searchbody" class="homepage2" onload="">
            <div class="header-new container" style="padding:1px;">
                <img src="http://ceumass.eps.uspceu.es/mediator/images/cembio_banner.jpg" alt="CEMBIO Banner" width="100%" />
            </div>
            
            <div id="div-nav">
                <nav id="nav">

                    <ul>
                        <li id="nav-li-img">
                            <img class="nav-img" src="http://ceumass.eps.uspceu.es/mediator/images/logoCMM.jpeg" alt="CEMBIO logo header" />
                        </li>
                        <li class="current">
                            <a href="/index.xhtml">Home</a>
                        </li>
                        <li>
                            <a href="">Search</a>
                            <ul>


                                <li>
                                    <a href="/simple_search.xhtml">Simple Search</a> 
                                </li>
                                <li>
                                    <a href="/advanced_search.xhtml">Advanced Search</a>
                                </li>
                                <li>
                                    <a href="/batch_search.xhtml">Batch Search</a>
                                </li>
                                <li>
                                    <a href="/batch_advanced_search.xhtml">Batch Advanced Search</a>
                                </li>
                                <li>
                                    <a href="/prolog_batch_advanced_search.xhtml">LAS service</a>
                                </li>
                                <li>
                                    <a href="/browse_search.xhtml">Browse Search</a>
                                </li>
                                <li>
                                    <a href="/msms_search.xhtml">MS/MS Search</a>
                                </li>
                            </ul>
                        </li>

                        <li>
                            <a href="/index_cesearch.xhtml">CE-MS Search</a>
                            <ul>
                                <li>
                                    <a href="/cems_exprmt_cesearch.xhtml">CE-MS EXP RMT Search</a> 
                                </li>
                                <li>
                                    <a href="/cems_effmob_cesearch.xhtml">CE-MS eff Mob Search</a> 
                                </li>
                                <li>
                                    <a href="/cems_mt1_cesearch.xhtml">CE-MS MT 1 marker</a> 
                                </li>
                                <li>
                                    <a href="/cems_mt2_cesearch.xhtml">CE-MS MT 2 markers</a> 
                                </li>
                                <li>
                                    <a href="/cems_rmt1_cesearch.xhtml">CE-MS RMT 1 marker</a> 
                                </li>
                                <li>
                                    <a href="/cems_rmt2_cesearch.xhtml">CE-MS RMT 2 markers</a> 
                                </li>
                                <li>
                                    <a href="/cems_targeted_exprmt_cesearch.xhtml">Targeted CE-MS EXP RMT Search</a> 
                                </li>
                                <li>
                                    <a href="/cems_targeted_effmob_cesearch.xhtml">Targeted CE-MS eff Mob Search</a> 
                                </li>
                            </ul>
                        </li>

                        <li>
                            <a href="/pathway_displayer.xhtml">Pathway Displayer</a>
                        </li>
                        <li>
                            <a href="">Oxidation</a>
                            <ul>
                                <li>
                                    <a href="/oxidation_lc_1.xhtml">Oxidation in long FA chain</a>
                                </li>
                                <li>
                                    <a href="/oxidation_sc_1.xhtml">Oxidation in short FA chain</a>
                                </li>
                            </ul>
                        </li>
                        <li>
                            <a href="/qa_inputtexts.xhtml">Spectral Quality</a>
                        </li>
                        <li>
                            <a href="/manuals.xhtml" target="_blank">Manual</a>
                        </li>
                    </ul>
                </nav>
            </div>

            <div id="intro-wrapper" class="wrapper style1">
                <div id="compresults">
</#macro>



<#macro footer component="">
                    <@macros.elementUpdateTime dates=component/>  
                </div>
            </div>

            <div id="version" class="wrapper style1">
                <div class="container warning">

                    <strong>CEU Mass Mediator version 3.0</strong>.
                    <br />
                    <strong>Citing CMM: </strong>
                    <ol>
                        <li>
                            <a href="https://www.sciencedirect.com/science/article/pii/S0731708517326559" target="_blank">
                                CMM 2.0: 
                                Gil de la Fuente A., Godzien J. et al. 
                                Knowledge-based metabolite annotation tool: CEU Mass Mediator.
                                Journal of Pharmaceutical and Biomedical Analysis, 2018, 154, 138-149.
                            </a>
                        </li>
                        <li>
                            <a href="https://pubs.acs.org/doi/abs/10.1021/acs.jproteome.8b00720" target="_blank">
                                CMM 3.0: 
                                Gil de la Fuente A., Godzien J. et al. 
                                CEU Mass Mediator 3.0: A Metabolite Annotation Tool
                                Journal of Proteome Research 2019 18 (2), 797-802 
                            </a>
                        </li>
                        <li>
                            <a href="https://pubs.acs.org/doi/abs/10.1021/acs.jproteome.8b00720" target="_blank">
                                CE-MS Database: 
                                Mamani-Huanca, M., Gil de la Fuente A. et al. 
                                Enhancing confidence of metabolite annotation in Capillary Electrophoresis-Mass Spectrometry untargeted metabolomics with relative migration time and in-source fragmentation
                                Journal of Crhomatography A 2020 1635 (4), 461758
                            </a>
                        </li>
                    </ol>
                    <br />
                    If you find 
                    any issue, we would love to hear about it. Please let us know at 
                    <script>getmailaddress('es', 'ceu', 'cmm.cembio');</script>
                    <br />
                    <strong>Last updated: 01/06/2020</strong>
                </div>
            </div>

            <div id="footer-wrapper" class="wrapper">
                <div class="title"><a href="http://www.uspceu.es/" target="_blank">Universidad CEU-San Pablo</a></div>
                <div id="footer" class="container">
                    <header class="style1">
                        <h3><a href="http://biolab.uspceu.com/" target="_blank">Laboratorio de Bioingeniería.
                                Escuela Politécnica Superior. Universidad CEU-San Pablo.</a></h3>
                        <h3><a href="http://www.metabolomica.uspceu.es/" target="_blank">
                                Center for Metabolomics and Bioanalysis - CEMBIO.
                                Universidad CEU-San Pablo.</a></h3>

                        <p>
                            Data has been obtained from: <br />
                            <a href="http://www.genome.jp/kegg/" target="_blank">KEGG</a>
                            <br />
                            <a href="https://metlin.scripps.edu/index.php" target="_blank">Metlin</a>
                            <br />
                            <a href="http://www.lipidmaps.org/" target="_blank">Lipid Maps</a>
                            <br />
                            <a href="http://www.hmdb.ca" target="_blank">HMDB</a>
                            <br />
                            <a href="http://minedatabase.mcs.anl.gov/#/home" target="_blank">MINE</a>
                        </p>
                    </header>

                    <hr />

                    <div class="row 150%">

                        <div class="6u 12u(mobile)">

                            <section>
                                <center>
                                    <a href="http://www.metabolomica.uspceu.es" target="_blank">
                                        <img src="http://ceumass.eps.uspceu.es/images/cembioLogo.png" alt="CEMBIO LOGO" height="200" />
                                    </a>
                                </center>
                            </section>
                        </div>

                        <div class="6u 12u(mobile)">

                            <!-- Contact -->
                            <section class="feature-list small">
                                <div class="row">
                                    <div class="6u 12u(mobile)">
                                        <section>
                                            <h3 class="icon fa-home">Mailing Address</h3>
                                            <p>CEMBIO<br />
                                                Universidad CEU-San Pablo. Urbanización Montepríncipe.<br />
                                                Carretera M-501 km 0. Boadilla del Monte.<br />
                                                28660 - MADRID
                                            </p>
                                        </section>
                                    </div>
                                    <div class="6u 12u(mobile)">
                                        <section>
                                            <h3 class="icon fa-envelope">Email</h3>
                                            <p><script>getmailaddress('es', 'ceu', 'cmm.cembio');</script></p>
                                        </section>
                                        <section>
                                            <h3 class="icon fa-phone">Phone</h3>
                                            <p>(+34) 913724711</p>
                                        </section>
                                    </div>
                                </div>
                            </section>

                        </div>
                    </div>
                    <hr />
                </div>
                <div id="copyright">
                    <ul>
                        <li>© Ceu Mass Mediator</li><li>Design: <a href="http://html5up.net">HTML5 UP</a></li>
                    </ul>
                </div>
            </div>

            <script src="/mediator/assets/js/jquery.min.js"></script>
            <script src="/mediator/assets/js/jquery-ui.min.js"></script>
            <script src="/mediator/assets/js/jquery.dropotron.min.js"></script>
            <script src="/mediator/assets/js/skel.min.js"></script>
            <script src="/mediator/assets/js/skel-viewport.min.js"></script>
            <script src="/mediator/assets/js/util.js"></script>
            <!--[if lte IE 8]&gt;&lt;script src="assets/js/ie/respond.min.js"&gt;&lt;/script&gt;&lt;![endif]-->
            <script src="/mediator/assets/js/main.js"></script></body>

        </html>
</#macro>


<#macro title id name>
    <p><span id="cid">[${id}]</span><span id="cname">${name}</span></p>
</#macro>

<#macro error>
    <p><span id="cname">Ops! Entity not found, please <a href="" onClick='window.history.back()'>go back.</a></span></p>
</#macro>


<#macro elementUpdateTime dates="">
    <#if (entity)?has_content>
        <div id="cfoot">
            <p>
                <#if (entity.getCreated())?has_content>
                    <span id="created">Created: ${entity.getCreated()}</span>
                    <br/>
                </#if>
                <#if (entity.getLast_updated())?has_content>
                    <span id="lastupdated">Last updated: ${entity.getLast_updated()}</span>
                </#if>
            </p>
        </div>  
    </#if>
</#macro>


<#macro attvalue name value="">
    <#if (value)?has_content>
        <div class="pair">
            <label class="complabname">${name}</label> 
            <input class="compvalue" type="text" value="${value}" readonly>
        </div>
    </#if>
</#macro>


<#macro attlongvalue name value="">
    <#if (value)?has_content>
        <div class="pair">
            <label class="complabname">${name}</label> 
            <textarea id="longvalue" class="compvalue" readonly>${value}</textarea>
        </div>
    </#if>
</#macro>


<#macro link name base="" id="">
    <#if (id)?has_content>
        <div class="pair">
            <label class="complabname">
                <a class="compref" href="${base}${id}" target="_blank" title="Open this site page.">${name}</a>
            </label> 
            <input class="compvalue" type="text" value="${id}" readonly>
        </div>
    </#if> 
</#macro>


<#macro linklist l name base="" sep=" -- ">
    <#if l?has_content>
        <fieldset class="attributes"><legend>${name}</legend>
            <div class="hugetext">
                <#list l as entity >
                    <#if entity.getId()?has_content>        
                        <a class="compref" href="${base}${entity.getId()}" target="_blank" title="Open this site page.}">${entity.getName()}</a>
                    <#else>
                        ${entity.getName()}
                    </#if>
                    <#if entity_has_next> ${sep} </#if>
                </#list>
            </div>
        </fieldset>
    </#if>
</#macro>

<#macro linknumlist l name base="" sep=" -- ">
    <#if l?has_content>
        <fieldset class="attributes"><legend>${name}</legend>
            <div class="hugetext">
                <#list l as entity >
                    <#if entity.getId()?has_content>        
                        <a class="compref" href="${base}${entity.getId()}" target="_blank" title="Open this site page.">(${entity.getNumber()}) ${entity.getName()}</a>
                    <#else>
                        (${entity.getNumber()}) ${entity.getName()}
                    </#if>
                    <#if entity_has_next> ${sep} </#if>
                </#list>
            </div>
        </fieldset>
    </#if>
</#macro>
