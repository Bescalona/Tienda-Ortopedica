function eliminar(){
	e.preventDefault();
	Swal.fire({
		  title: 'Are you sure?',
		  text: "You won't be able to revert this!",
		  type: 'warning',
		  showCancelButton: true,
		  confirmButtonColor: '#3085d6',
		  cancelButtonColor: '#d33',
		  confirmButtonText: 'Yes, delete it!'
		}).then((result) => {
		  if (result.value) {
		    Swal.fire(
		      'Deleted!',
		      'Your file has been deleted.',
		      'success'
		    )
		    /*console.log("elId: "+elId);
		    var element = document.querySelector('a[href="#"]');
		    element.href = "/factura/eliminar/" +elId;
		    document.getElementById(eliminarFactura).href="/factura/eliminar/" +elId;*/
		    
		  
		  }
		})
}