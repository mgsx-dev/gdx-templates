#!/usr/bin/ruby
require 'erb'
require 'yaml'
require 'fileutils'
require 'colorize'
# require 'pp'

# list variables in an expression of type : "...$(var1)...$(var2)...$"
def parse_expression(expression)
    expression.scan(/\$\((\w+)\)/).map{|c,*_|c}.uniq
end

# evaluate expression of type "...$(var1)...$(var2)...$"
def do_expression(expr, vars)
    parse_expression(expr).each do |k|
        expr = expr.gsub("$(#{k})", vars[k])
    end
    expr
end

# split mapping expression : "source:destination"
def parse_mapping(expression)
    expression.scan(/^(.+):(.+)$/).first
end

def inject_array(hash, vars, v=nil)
    vars.each{|k| hash[k] = v}
end
def inject_hash(hash, vars)
    vars.each{|k,v| hash[k] = v}
end

def parse_config(result, template_path)
    config_path = "#{template_path}/.scaffold.yml"
    return unless File.exists?(config_path)
    meta = YAML.load_file(config_path)
        
    (meta["files"] || []).each do |v|
        src, dst = parse_mapping(v)
        result[:files]["#{template_path}/#{src}"] = dst
        inject_array result[:vars], parse_expression(dst)
    end

    (meta["folders"] || []).each do |v|
        src, dst = parse_mapping(v)
        result[:folders]["#{template_path}/#{src}"] = dst
        inject_array result[:vars], parse_expression(dst)
    end

    inject_hash result[:defaults], (meta["defaults"] || {})
end

def parse_template(template_path)

    result = {
        defaults: {},
        vars: {},
        files: {},
        folders: {}
    }

    if File.exists?("#{template_path}/.scaffold.yml")
        parse_config(result, template_path)
    end

    (Dir.glob("#{template_path}/**/*").to_a + Dir.glob("#{template_path}/**/.*").to_a).each do |file|
        relative_file = file.split(template_path + '/', 2).last
        if relative_file == '.scaffold.yml' or File.directory?(file) then
            # skip
        elsif File.extname(file) == ".erb" then
            target = File.dirname(relative_file) + '/' + File.basename(relative_file, File.extname(file))
            File.read(file).scan(/<%= *(\w+) *%>/) do |varName, *_|
                result[:vars][varName] = nil
            end
            result[:files][file] = target unless result[:files][file]
        else
            result[:files][file] = relative_file unless result[:files][file]
        end
    end

    uniVars = result[:vars].map{|k,v| k}.uniq.map{|e|
       e.scan(/(\w+)(Name|Path|CamelUp|CamelDown)/).map{|v,*_| v}.first
    }.select{|k,v|k}
    
    result[:vars] = uniVars.inject({}){|r,e| r[e] = result[:vars][e]; r}
    
    return result
end

def do_template(template, infolder, outfolder, vars)
    vars.each do |k,v|
        if template[:vars].has_key?(k)
            template[:vars][k] = v
            template[:vars]["#{k}Name"] = v
            template[:vars]["#{k}Path"] = v.gsub(/\./, "/")
            template[:vars]["#{k}CamelUp"] = v
            template[:vars]["#{k}CamelDown"] = v
        else
            raise "unrecognized var #{k}"
        end
    end
    template[:folders].each do |src,dst|
        template[:folders][src] = "#{do_expression(dst, template[:vars])}"
    end

    template[:files].each do |src,dst|
        dir = File.dirname(src.split(infolder + '/', 2).last)
        dir = "" if dir == File.basename(infolder) || dir == "."
        outdir = if template[:folders][File.dirname(src)] then
            template[:folders][File.dirname(src)] + "/"
        else
            dir == "" ? dir : dir + "/"
        end
        template[:files][src] = "#{outfolder}/#{outdir}#{do_expression(File.basename(dst), template[:vars])}"
    end
    template
end



cmd, template_path, output_path, *options = ARGV

vars = {}
options.each do |opt|
    varName, varValue = opt.scan(/(\w+)=(.*)/).first
    vars[varName] = varValue
end

cmd = nil if cmd == 'help' && template_path == nil

case cmd

when 'list' 
    Dir.glob("templates/*") do |file|
        puts "#{__FILE__} help #{file}"
    end

when 'help'
    config = parse_template(template_path)
    inject_hash config[:vars], config[:defaults]
    help = [__FILE__, 'generate', template_path, output_path||'out']
    config[:vars].each do |k,v|
        help << "#{k}=#{vars[k]||v||'value'}"
    end
    puts "Example :\n" + help.join(" ").light_black

when 'generate', 'override', 'test'
    
    test = cmd == 'test'
    override = cmd == 'override'

    config = parse_template(template_path)

    do_template(config, template_path, output_path, vars)

    unless (missing = config[:vars].select{|k,v| v == nil}.map(&:first)).empty?
        puts "Missing parameters : " + missing.join(', ').red
        exit
    end

    config[:files].each do |src,dst|
        info = (test ? '(test) ' : 'write ')+"#{src} => #{dst}"
        skip = test
        FileUtils.makedirs File.dirname(dst) unless skip
        if File.extname(src) == ".erb" then
            
            b = binding
            config[:vars].each do |k,v|
                b.local_variable_set(k, v)
            end
            content = ERB.new(File.read(src)).result(b)

            if File.exists? dst 
                if File.read(dst) == content
                    puts (info + " (identical)").yellow
                    skip = true
                else
                    if override
                        puts (info + " (override)").red
                    else
                        puts (info + " (conflicts)").red
                        skip = true
                    end
                end
            else
                puts (info + " (generate)").green
            end
            File.write(dst, content) unless skip
        else
            if File.exists? dst 
                if FileUtils.identical? src, dst then
                    puts (info + " (identical)").yellow
                    skip = true
                else
                    if override
                        puts (info + " (override)").red
                    else
                        puts (info + " (conflicts)").red
                        skip = true
                    end
                end
            else
                puts (info + " (copy)").green
            end
            FileUtils.cp src, dst unless skip
        end
    end

else
    puts "usage : #{__FILE__} list"
end
