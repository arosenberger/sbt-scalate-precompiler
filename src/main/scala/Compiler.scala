package pragmagica.scalate

import java.io.{File, FileInputStream}
import scala.io.Source
import scala.collection.Set

import org.slf4j.LoggerFactory

import org.fusesource.scalate.{TemplateSource, Binding, TemplateEngine}
import org.fusesource.scalate.servlet.ServletRenderContext
import org.fusesource.scalate.util.{FileResourceLoader, IOUtil}



/**  Generates Scala sources from Scalate templates
 *   Based on code from Yasushi Abe's http://github.com/Yasushi/scalate-cli
 */
object Generator {

  val logger = LoggerFactory.getLogger("ScalateGenerator")

  def main(args:Array[String]) {

    assert(args.size >= 3, "Need at least two parameters: output, imports file, sourceRoot, [sourceRoot, ...]")
    val sources = args.drop(2).toList

    precompile(sources, args.head, args(1))

  }

  def precompile(sources:List[String], out:String, importsPath: String) = {
    val output = new File(out)
    output.mkdirs()

    var engine = new TemplateEngine
    engine.resourceLoader = new FileResourceLoader(None)

    val imports = Source.fromInputStream(new FileInputStream(new File(importsPath))).getLines.toList
    engine.importStatements = engine.importStatements ::: imports ::: Nil

    val compiled = collection.mutable.ListBuffer[File]()
    
    for( source <- sources ) {

      val paths = find(source, engine.codeGenerators.keySet)

      for( file <- paths ) {

        val uri = buildUri(source,file)
        println( "Generating source for "+file+" and with uri " + uri )
        
        val src = TemplateSource.fromFile(file,uri)
        src.engine = engine
        val code = engine.generateScala(src, Nil)
        val sourceFile  = new File(output, sourceName(source, file))

        logger.debug( "Generating source in "+ sourceFile )
        sourceFile.getParentFile.mkdirs
        IOUtil.writeBinaryFile(sourceFile, code.source.getBytes("UTF-8"))
        compiled += sourceFile
      }
    }

    compiled.toList
  }

  private def sourceName( source:String, file:File ) = 
    file.getParent.replace(stripSlash(source),"")+"/"+ file.getName+".scala"

  private def buildUri( source:String, file:File ) = 
    file.getParent.replace(stripSlash(source),"")+"/"+file.getName

  private def stripSlash(path:String) = 
    if(path.endsWith("/")) path.take(path.size-1)
    else path

  private def find(root:String, exts:Set[String] = Set.empty):List[File] = {
    val rootDir = new File(root)
    require( rootDir.exists && rootDir.isDirectory, 
              "root must be a directory name: " + root )
    scan(rootDir).filter(filterExt(_,exts))
  }

  private def scan(basedir:File):List[File] = {
    assert( basedir.canRead, "Can't read "+basedir )
    val (dirs,files) = basedir.listFiles.toList.partition(_.isDirectory)
    logger.debug(basedir.toString)
    (files ++ dirs.flatMap(scan(_))) 
  }

  private def filterExt(f:File, exts:Set[String]) = 
    if(exts.isEmpty) true
    else exts.find(_ == (f.getName.split("""\.""").last)).isDefined


}
