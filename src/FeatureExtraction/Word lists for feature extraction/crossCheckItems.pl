open(IN1, "Connectives.txt") or die "cannot open in1";
open(OUT, ">ConnectivesFiltered.txt") or die "cannot open out";

my %mapping = ();
while (<IN1>)
{
	chomp();
	$mapping{$_}++;
}
close(IN1);

foreach (sort keys %mapping)
{
	print OUT "$_\n";
}
close(OUT);