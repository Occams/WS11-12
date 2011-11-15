data = []

-2.upto(2) do |y|
	data[y+2] = []
	-2.upto(2) do |x|
		data[y+2] << (-2*Math.sqrt(x*x+y*y)).round + 6
		puts "#{x} #{y}: #{-2*Math.sqrt(x*x+y*y)}"
	end
end

data.each do |d|
	puts d.join(" & ") + "\\\\"
end
