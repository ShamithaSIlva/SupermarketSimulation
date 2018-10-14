package simulator.entity;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.JTextField;

import simulator.demos.Demo;
import simulator.util.RandomNumberGenerator;

/**
 * @author Shamitha This class acts as the real world Checkout of a Supermarket
 */
public class CheckoutQueue implements Runnable
{
	private Queue<Customer> customers;
	private RandomNumberGenerator generator;
	private String checkOutName;
	private int queueId;	
	private double totalCustomerWaitingTime;
	private int totalCustomersProcessed;

	public CheckoutQueue()
	{
		customers = new LinkedList<>();
		generator = new RandomNumberGenerator();
	}
	
	@Override
	public void run()
	{
		System.out.println( Thread.currentThread().getName() + " started..!!" );
		while ( true )
		{
			Customer customer = null;
			// System.out.println("Current Customers in Checkout Queue : "+Thread.currentThread().getName()+" ##" + customers.size());
			synchronized ( customers )
			{
				if ( customers.size() == 0 )
				{
					try
					{
						customers.wait();
					}
					catch ( InterruptedException e )
					{
						e.printStackTrace();
					}
				}
				else
				{
					customer = customers.poll();
					totalCustomersProcessed++;
					JTextField textField = Demo.getDemoInstance().getUi().getCheckOutAssociationMap().get( this.getQueueId() );
					//int currentValue = Integer.valueOf(textField.getText());
					textField.setText( Integer.toString( customers.size() ));
					customers.notifyAll();
				}

			}
			if ( customer != null )
			{
				int trolleyProductCount = customer.getTrolley().getProductCount();
				for ( int i = 0; i < trolleyProductCount; i++ )
				{
					System.out.println( "Customer is being processed.." );
					double tempTime = generator.getRandomDecimalNumberInRange( 0.5, 6 );
					long scanTime = ( long ) tempTime * 1000;
					DecimalFormat df = new DecimalFormat( "#.##" );
					//System.out.println( df.format( tempTime ) );
					totalCustomerWaitingTime += scanTime;
					try
					{
						Thread.sleep( scanTime );
					}
					catch ( InterruptedException e )
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				totalCustomerWaitingTime += customer.getWaitingTimeInQueue( System.currentTimeMillis() );
				System.out.println( "Customer ID : "+customer.getCustomerId()+" total waiting time in queue : " + customer.getWaitingTimeInQueue( System.currentTimeMillis() )+" ## Product count : "+customer.getTrolley().getProductCount() );
			}
		}
	}

	public String getCheckOutName()
	{
		return checkOutName;
	}
	
	public void setCheckOutName( String checkOutName )
	{
		this.checkOutName = checkOutName;
	}
	
	public Queue<Customer> getCustomers()
	{
		return customers;
	}
	
	public void setCustomers( Queue<Customer> customers )
	{
		this.customers = customers;
	}
	
	public int getQueueId()
	{
		return queueId;
	}

	public void setQueueId( int queueId )
	{
		this.queueId = queueId;
	}
}
