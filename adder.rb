class Adder
	def initialize(num)
		@num = num
    end
	def method_missing(methodName, *args, &block)
		#puts "called method_missing"#if you uncomment this line you can see that method_missing is not called when x.plus3 is called the second time
		if(methodName[/plus\d\d*\z/] && args.count == 0) then
			toAdd = methodName[/\d\d*/]
			newMethod = "def #{methodName}; puts  #{@num} + #{toAdd}; end;"
			Adder.class_eval newMethod
			eval "plus#{toAdd}"
			else
			super.method_missing(methodName, *args, &block)
		end
	end	
end
x = Adder.new(2)
x.plus3
x.plus3
y = Adder.new(399)
y.plus1000

puts "THIS IS TO TEST FOR GITHUB TEST 2222222"
