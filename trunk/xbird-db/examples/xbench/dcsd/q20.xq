(:
 DC/SD_Q20 Retrieve the item title whose size (length*width*height) is bigger than certain number (500000).
:)

for $size in input()/catalog/item/attributes/size_of_book
where zero-or-one($size/length) * zero-or-one($size/width) * zero-or-one($size/height) > 500000
return
    <Output>
        {$size/../../title}
    </Output>
